package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.request.SaveReadmeReq;
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


    @BeforeEach
    void setUp(){
        readmeService = new ReadmeService(githubClient, openAiClient, githubTokenDao,readmeRepository,userRepository);
    }

    @Test
    void 리포지토리_목록_조회(){
        RepositoryInfo info1 = new RepositoryInfo("name1", "url1");
        RepositoryInfo info2 = new RepositoryInfo("name2", "url2");
        RepositoryInfo[] repos = {info1, info2};
        given(githubTokenDao.findByGithubId("id")).willReturn("token");
        given(githubClient.findRepositories("token")).willReturn(repos);

        ReadRepositoriesRes res = readmeService.readRepositories("id");

        assertEquals("name1", res.getRepositories().get(0).getName());
        assertEquals("url1", res.getRepositories().get(0).getUrl());
        assertEquals("name2", res.getRepositories().get(1).getName());
        assertEquals("url2", res.getRepositories().get(1).getUrl());
    }

    @Test
    void 리드미_저장(){
        User user = User.builder().id(1L).build();
        given(userRepository.findByGithubId("user")).willReturn(Optional.of(user));
        given(githubTokenDao.findByGithubId("user")).willReturn("token");
        given(githubClient.findRepositories("token"))
                .willReturn(new RepositoryInfo[]{new RepositoryInfo("repo1","url1")});
        ArgumentCaptor<Readme> captor = ArgumentCaptor.forClass(Readme.class);

        SaveReadmeReq req = new SaveReadmeReq("repo1","readme1");

        readmeService.saveReadme("user",req);

        then(readmeRepository).should().save(captor.capture());

        assertEquals("repo1", captor.getValue().getName());
        assertEquals(1L, captor.getValue().getUser().getId());
    }
}
