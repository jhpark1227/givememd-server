package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.CommitFileDto;
import junhyeok.giveme.readme.dto.request.CommitReadMeReq;
import junhyeok.giveme.readme.dto.request.SaveReadmeReq;
import junhyeok.giveme.readme.dto.request.UpdateReadmeReq;
import junhyeok.giveme.readme.dto.response.FileRes;
import junhyeok.giveme.readme.entity.Readme;
import junhyeok.giveme.readme.repository.ReadmeRepository;
import junhyeok.giveme.user.dao.GithubTokenDao;
import junhyeok.giveme.readme.dto.response.ReadRepositoriesRes;
import junhyeok.giveme.readme.dto.response.RepositoryInfo;
import junhyeok.giveme.user.entity.User;
import junhyeok.giveme.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ReadmeServiceTest {
    private ReadmeService readmeService;

    @Mock
    GithubClient githubClient;

    @Mock
    OpenAiClient openAiClient;

    @Mock
    GithubTokenDao githubTokenDao;

    @Mock
    ReadmeRepository readmeRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ReadmeQueryService readmeQueryService;

    @Mock
    EvaluationService evaluationService;

    @Mock
    ReadmeAsyncService readmeAsyncService;


    @BeforeEach
    void setUp(){
        readmeService = new ReadmeService(githubClient, openAiClient, evaluationService, githubTokenDao,readmeRepository, userRepository,readmeQueryService, readmeAsyncService);
    }

    @Test
    void 리포지토리_목록_조회(){
        RepositoryInfo info1 = new RepositoryInfo("name1", "url1");
        RepositoryInfo info2 = new RepositoryInfo("name2", "url2");
        RepositoryInfo[] repos = {info1, info2};
        given(githubTokenDao.findById(1L)).willReturn("token");
        given(githubClient.findRepositories("token")).willReturn(repos);

        ReadRepositoriesRes res = readmeService.readRepositories(1L);

        assertEquals("name1", res.getRepositories().get(0).getName());
        assertEquals("url1", res.getRepositories().get(0).getUrl());
        assertEquals("name2", res.getRepositories().get(1).getName());
        assertEquals("url2", res.getRepositories().get(1).getUrl());
    }

    @Test
    void 리드미_저장(){
        User user = User.builder().id(1L).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(githubTokenDao.findById(1L)).willReturn("token");
        given(githubClient.findRepositories("token"))
                .willReturn(new RepositoryInfo[]{new RepositoryInfo("repo1","url1")});
        ArgumentCaptor<Readme> captor = ArgumentCaptor.forClass(Readme.class);

        SaveReadmeReq req = new SaveReadmeReq("repo1","readme1");

        readmeService.saveReadme(1L,req);

        then(readmeRepository).should().save(captor.capture());

        assertEquals("repo1", captor.getValue().getName());
        assertEquals(1L, captor.getValue().getUser().getId());
    }

    @Test
    void 리드미_내용_변경(){
        Readme readme = Readme.builder().id(1L).build();
        UpdateReadmeReq req = new UpdateReadmeReq("newReadme");
        given(readmeQueryService.validateOwner(1L,1L)).willReturn(true);
        given(readmeRepository.findById(1L)).willReturn(Optional.of(readme));
        readmeService.updateReadme(1L,1L, req.getContent());

        Assertions.assertEquals("newReadme", readme.getContent());
    }

    @Test
    void 새로운_파일_커밋(){
        given(githubTokenDao.findById(1L)).willReturn("token");
        RepositoryInfo info = new RepositoryInfo("repo1", "url1");
        given(githubClient.findRepositories("token")).willReturn(new RepositoryInfo[]{info});
        User user = User.builder().id(1L).githubId("githubId").email("email").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(githubClient.loadFileInfo(any())).willReturn(null);

        ArgumentCaptor<CommitFileDto> captor = ArgumentCaptor.forClass(CommitFileDto.class);

        CommitReadMeReq req = new CommitReadMeReq("repo1", "content1");
        readmeService.commitReadme(1L, req);

        then(githubClient).should().commitFile(captor.capture());
        Assertions.assertEquals("repo1",captor.getValue().getRepositoryName());
        Assertions.assertNull(captor.getValue().getSha());
    }

    @Test
    void 리드미_생성(){
        RepositoryInfo[] infos = {new RepositoryInfo("repo1", "url")};
        given(githubTokenDao.findById(1L)).willReturn("token");
        given(githubClient.findRepositories("token")).willReturn(infos);
        FileRes[] fileRes = new FileRes[10];
        fileRes[0] = new FileRes("file1.js", "file", "url1", null);
        fileRes[1] = new FileRes("file2.js", "file", "url1", null);
        fileRes[2] = new FileRes("file3.js", "file", "url1", null);
        fileRes[3] = new FileRes("file4.js", "file", "url1", null);
        fileRes[4] = new FileRes("file5.js", "file", "url1", null);
        given(readmeAsyncService.summarizeFile("token","url1")).willReturn(CompletableFuture.completedFuture("response"));
        given(githubClient.readDirectory(anyString(),anyString())).willReturn(fileRes);
        given(openAiClient.sendMessage(any())).willReturn("response");

        readmeService.createReadme(1L, "repo1");
    }
}
