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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    private final ReadmeAsyncService readmeAsyncService;

    private static final String[] extensionList = {"java","js","html","py","c","cpp","php","swift","go","r","kt","rs","ts","gradle"};
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

        List<CompletableFuture<String>> responses = new ArrayList<>();
        collectSummary(responses, token, url+"/contents");

        CompletableFuture<List<String>> result = CompletableFuture.allOf(responses.toArray(new CompletableFuture[0]))
                .thenApply(v -> responses.stream().map(CompletableFuture::join).collect(Collectors.toList()));

        List<Message> messages = new ArrayList<>(result.join().stream().map(x->new Message("user",x)).toList());

        String readme = summarizeProject(messages);

        evaluationService.saveEvaluation(url, readme);

        return new CreateReadmeRes(repositoryName, readme);
    }

    private String summarizeProject(List<Message> summaries){
        List<Message> segments = new ArrayList<>();

        final int MAX_CHARACTERS = 10000;

        String code = "";
        for(Message summary : summaries){
            int summaryLength = summary.getContent().length();
            if (code.length() + summaryLength <= MAX_CHARACTERS){
                code = code +'\n' + summary.getContent();
            }
            else{
                segments.add(new Message("system", "프로젝트에 대한 소개 및 실행방법, 핵심 기능, 기술스택(개발 언어, 개발 환경, 클라우드)을 어떤 것들을 쓰는지 중점적으로 파악하고 이것들을 중심으로 마크다운 문법을 지키며 readme.md 파일을 작성 후 사용자에게 전달해야해. 항목,소제목마다 아이콘과 이모티콘을 사용해야하고 기술스택은 배지를 통해 적어야만 해."+code));
                code = "";
            }
        }

        List<Message> messages = new ArrayList<>();
        for(Message segment : segments){
            String script = openAiClient.sendMessage(List.of(segment));

            messages.add(new Message("user", script));
        }

        messages.add(new Message("system", "여러개로 된 readme를 보고 배지, 아이콘을 적극 사용해서 아주 자세히 하나의 readme로 만들어줘. 기술스택은 무조건 배지로 해줘."));

        return openAiClient.sendMessage(messages);
    }

    private void collectSummary(List<CompletableFuture<String>> responses, String token, String url){
        FileRes[] files = githubClient.readDirectory(token, url);
        try{
            for(int i=0;i<files.length;i++){

                FileRes file = files[i];
                String name = file.getName();
                String type = file.getType();

                if(type.equals("file")){
                    if(name.split("\\.").length!=2) continue;

                    String extension = name.split("\\.")[1];
                    String downloadUrl = file.getDownloadUrl();

                    if(Arrays.asList(extensionList).contains(extension)){
                        responses.add(readmeAsyncService.summarizeFile(token, downloadUrl));
                    }
                }else if(type.equals("dir")){
                    collectSummary(responses, token, url+"/"+name);
                }
            }
        }catch (Exception e){
            e.getStackTrace();
        }
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
