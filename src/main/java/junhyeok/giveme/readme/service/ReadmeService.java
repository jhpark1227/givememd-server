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

        String readme = convertToReadme(summarizeProject(messages));

        evaluationService.saveEvaluation(url, readme);

        return new CreateReadmeRes(repositoryName, readme);
    }

    private String summarizeProject(List<Message> messages){
        messages.add(new Message("system", "너는 각 소스코드 파일의 내용을 담은 다수의 메시지를 읽고 전체 프로젝트의 정보를 한국어로 제시하는 챗봇이다."));
        messages.add(new Message("user","위 문장들을 읽어보고 응답의 목차, 전체 프로젝트의 설명, 설치 및 실행방법, 5~10개 정도의 핵심 기능, 기술스택(언어, 개발 환경, 클라우드)을 자세하게 파악해서 다음 형식에 맞춰 작성해줘. 1.목차:<목차>, 2.설명:<전체 프로젝트의 설명>, 3.설치 및 실행 방법:<설치 및 실행방법>, 4.핵심 기능:1.<추출할 기능1> 2.<추출할 기능2> 3.<추출할 기능1> 4.<추출할 기능4> 5.<추출할 기능5>, 5.기술 스택:5-1.언어:<추출할 개발언어> 5-2.개발환경:<추출할 개발환경> 5-3.클라우드:<추출할 클라우드>"));

        String res = openAiClient.sendMessage(messages);

        return res;
    }

    private String convertToReadme(String summary){
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "너는 프로젝트의 정보를 읽고 Readme.md 마크다운으로 변환하는 챗봇이다."));
        messages.add(new Message("user", "세 개의 백틱으로 구분된 프로젝트 정보를 읽고 Readme.md 마크다운으로 변환해줘. 프로젝트 정보 : ```"+summary+"```"));
        String res = openAiClient.sendMessage(new ArrayList<>(messages));

        return res;
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
