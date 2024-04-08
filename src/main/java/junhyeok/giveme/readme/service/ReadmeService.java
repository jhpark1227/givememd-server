package junhyeok.giveme.readme.service;

import jakarta.transaction.Transactional;
import junhyeok.giveme.readme.dto.request.Message;
import junhyeok.giveme.readme.dto.request.SaveReadmeReq;
import junhyeok.giveme.readme.dto.request.UpdateReadmeReq;
import junhyeok.giveme.readme.dto.response.*;
import junhyeok.giveme.readme.entity.Readme;
import junhyeok.giveme.readme.exception.CanNotAccessReadmeException;
import junhyeok.giveme.readme.exception.NotExistRepositoryException;
import junhyeok.giveme.readme.exception.ReadmeExistException;
import junhyeok.giveme.readme.repository.ReadmeRepository;
import junhyeok.giveme.user.dao.GithubTokenDao;
import junhyeok.giveme.user.entity.User;
import junhyeok.giveme.user.exception.UserNotExistException;
import junhyeok.giveme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service @Transactional
@RequiredArgsConstructor
public class ReadmeService {
    private final GithubClient githubClient;
    private final OpenAiClient openAiClient;
    private final GithubTokenDao githubTokenDao;
    private final ReadmeRepository readmeRepository;
    private final UserRepository userRepository;
    private final ReadmeQueryService readmeQueryService;

    private static final String[] extensionList = {"java","js","html","py","c","cpp","php","swift","go","r","kt","rs","ts"};


    public ReadRepositoriesRes readRepositories(String userId){
        String githubToken = githubTokenDao.findByGithubId(userId);

        RepositoryInfo[] repos = githubClient.findRepositories(githubToken);

        return new ReadRepositoriesRes(repos);
    }

    public CreateReadmeRes createReadme(String userId, String url){
        String repoName = url.split("/")[url.split("/").length-1];
        String token = githubTokenDao.findByGithubId(userId);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system","You are a helpful assistant for summarizing project codes."));

        collectSummary(messages, token, url+"/contents");
        return new CreateReadmeRes(repoName, summarizeProject(messages));
    }

    private String summarizeProject(List<Message> messages){
        messages.add(new Message("system", "너는 소스 코드의 내용을 요약한 텍스트들을 제공하면 그걸 바탕으로 깃허브 readme.md 파일로 만들어주는 챗봇이다. 프로젝트에 대한 설명,목차,설치 및 실행방법, 핵심 기능, 기술스택(개발 언어, 개발 환경, 클라우드)을 어떤 것들을 쓰는지 중점적으로 파악하고 이것들을 중심으로 마크다운 문법을 지키며 readme.md 파일을 작성 후 사용자에게 전달해야해"));
        messages.add(new Message("user","이 요약된 글들을 보고 readme.md 파일을 만들어줘."));

        String res = openAiClient.sendMessage(messages);

        return res;
    }

    private void collectSummary(List<Message>messages, String token, String url){
        FileRes[] files = githubClient.readDirectory(token, url);

        for(int i=0;i<files.length;i++){

            FileRes file = files[i];
            String name = file.getName();
            String type = file.getType();

            if(type.equals("file")){
                if(name.split("\\.").length!=2) continue;

                String extension = name.split("\\.")[1];
                String downloadUrl = file.getDownloadUrl();

                if(Arrays.asList(extensionList).contains(extension)){
                    messages.add(new Message("user", summarizeFile(token, downloadUrl)));
                }
            }else if(type.equals("dir")){
                collectSummary(messages, token, url+"/"+name);
            }
        }
    }

    private String summarizeFile(String token, String url){
        String file = githubClient.readFile(token, url);
        Message message = new Message("user", "Summarize this: "+file);

        String res = openAiClient.sendMessage(Arrays.asList(message));
        return res;
    }

    public void saveReadme(String userId, SaveReadmeReq req){
        verifyRepositoryName(userId, req.getName());

        User user = userRepository.findByGithubId(userId)
                .orElseThrow(UserNotExistException::new);

        if(readmeRepository.findByNameAndUser(req.getName(), user).isPresent()){
            throw new ReadmeExistException();
        }

        readmeRepository.save(Readme.builder()
                .content(req.getContent())
                .user(user)
                .name(req.getName()).build());
    }

    private void verifyRepositoryName(String userId, String repoName){
        String token = githubTokenDao.findByGithubId(userId);

        RepositoryInfo[] repos = githubClient.findRepositories(token);
        Arrays.stream(repos).forEach(repo->System.out.println(repo.getName()));
        if(!Arrays.stream(repos).anyMatch(repo->repo.getName().equals(repoName))){
            throw new NotExistRepositoryException();
        }
    }

    public void updateReadme(String userId, UpdateReadmeReq req){
        if(!readmeQueryService.validateOwner(userId, req.getReadmeId())){
            throw new CanNotAccessReadmeException();
        }

        Readme readme = readmeRepository.findById(req.getReadmeId())
                        .orElseThrow(ReadmeExistException::new);

        readme.changeContent(req.getContent());
    }
}
