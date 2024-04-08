package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.request.SaveReadmeReq;
import junhyeok.giveme.readme.dto.request.UpdateReadmeReq;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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


    @BeforeEach
    void setUp(){
        readmeService = new ReadmeService(githubClient, openAiClient, githubTokenDao,readmeRepository,userRepository,readmeQueryService);
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
}
