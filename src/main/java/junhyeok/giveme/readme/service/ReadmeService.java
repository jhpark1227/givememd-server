package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.request.CommitReadMeReq;
import junhyeok.giveme.readme.dto.request.Message;
import junhyeok.giveme.readme.dto.request.SaveReadmeReq;
import junhyeok.giveme.readme.dto.response.*;
import junhyeok.giveme.readme.entity.Readme;
import junhyeok.giveme.readme.exception.CanNotAccessReadmeException;
import junhyeok.giveme.readme.exception.NotExistRepositoryException;
import junhyeok.giveme.readme.exception.ReadmeExistException;
import junhyeok.giveme.readme.repository.ReadmeRepository;
import junhyeok.giveme.user.dao.GithubTokenDao;
import junhyeok.giveme.readme.dto.CommitFileDto;
import junhyeok.giveme.readme.dto.LoadFileInfoDto;
import junhyeok.giveme.user.entity.User;
import junhyeok.giveme.user.exception.UserNotExistException;
import junhyeok.giveme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service @Transactional
@RequiredArgsConstructor
public class ReadmeService {
    private final GithubClient githubClient;
    private final OpenAiClient openAiClient;
    private final EvaluationService evaluationService;
    private final GithubTokenDao githubTokenDao;
    private final ReadmeRepository readmeRepository;
    private final UserRepository userRepository;
    private final ReadmeQueryService readmeQueryService;

    private static final String[] extensionList = {"java","js","html","py","c","cpp","php","swift","go","r","kt","rs","ts"};
    private static final String readmeFileName = "README.md";
    private static final String commitMessage = "Update README.md";

    public ReadRepositoriesRes readRepositories(Long userId){
        RepositoryInfo[] repos = loadRepositories(userId);

        return new ReadRepositoriesRes(repos);
    }

    private RepositoryInfo[] loadRepositories(Long userId){
        String githubToken = githubTokenDao.findById(userId);

        return githubClient.findRepositories(githubToken);
    }

    public CreateReadmeRes createReadme(Long userId, String repositoryName){
        RepositoryInfo[] repos = loadRepositories(userId);

        String url = null;
        for(RepositoryInfo repo : repos){
            if(repo.getName().equals(repositoryName)) url = repo.getUrl();
        }
        if(url==null) throw new NotExistRepositoryException();

        String token = githubTokenDao.findById(userId);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system","You are a helpful assistant for summarizing project codes."));

        collectSummary(messages, token, url+"/contents");

        String readme = summarizeProject(messages);

        evaluationService.saveEvaluation(url, readme);

        return new CreateReadmeRes(repositoryName, readme);
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

    public void saveReadme(Long userId, SaveReadmeReq req){
        verifyRepositoryName(userId, req.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotExistException::new);

        if(readmeRepository.findByNameAndUser(req.getName(), user).isPresent()){
            throw new ReadmeExistException();
        }

        readmeRepository.save(Readme.builder()
                .content(req.getContent())
                .user(user)
                .name(req.getName()).build());
    }

    private void verifyRepositoryName(Long userId, String repoName){
        String token = githubTokenDao.findById(userId);

        RepositoryInfo[] repos = githubClient.findRepositories(token);
        Arrays.stream(repos).forEach(repo->System.out.println(repo.getName()));
        if(!Arrays.stream(repos).anyMatch(repo->repo.getName().equals(repoName))){
            throw new NotExistRepositoryException();
        }
    }

    public void updateReadme(Long userId, Long readmeId, String content){
        if(!readmeQueryService.validateOwner(userId, readmeId)){
            throw new CanNotAccessReadmeException();
        }

        Readme readme = readmeRepository.findById(readmeId)
                        .orElseThrow(ReadmeExistException::new);

        readme.changeContent(content);
    }

    public void commitReadme(Long userId, CommitReadMeReq req){
        verifyRepositoryName(userId, req.getRepositoryName());

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotExistException::new);

        String accessToken = githubTokenDao.findById(userId);

        FileRes file = githubClient.loadFileInfo(
                    LoadFileInfoDto.builder()
                    .accessToken(accessToken)
                    .githubId(user.getGithubId())
                    .repositoryName(req.getRepositoryName())
                    .fileName(readmeFileName).build()
        );

        CommitFileDto dto = CommitFileDto.builder()
                .accessToken(accessToken)
                .repositoryName(req.getRepositoryName())
                .fileName(readmeFileName)
                .message(commitMessage)
                .author(new CommitFileDto.Author(user.getGithubId(), user.getEmail()))
                .content(Base64.encodeBase64String(req.getContent().getBytes())).build();

        if(file!=null){
            dto.addSha(file.getSha());
        }

        githubClient.commitFile(dto);
    }
}
