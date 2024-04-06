package junhyeok.giveme.readme.service;

import junhyeok.giveme.user.dao.GithubTokenDao;
import junhyeok.giveme.readme.dto.response.ReadRepositoriesRes;
import junhyeok.giveme.readme.dto.response.RepositoryInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReadmeServiceTest {
    private ReadmeService readmeService;

    @Mock
    GithubClient githubClient;

    @Mock
    OpenAiClient openAiClient;

    @Mock
    GithubTokenDao githubTokenDao;


    @BeforeEach
    void setUp(){
        readmeService = new ReadmeService(githubClient, openAiClient, githubTokenDao);
    }

    @Test
    void 리포지토리_목록_조회(){
        RepositoryInfo info1 = new RepositoryInfo("name1", "url1");
        RepositoryInfo info2 = new RepositoryInfo("name2", "url2");
        RepositoryInfo[] repos = {info1, info2};
        BDDMockito.given(githubTokenDao.findByGithubId("id")).willReturn("token");
        BDDMockito.given(githubClient.findRepositories("token")).willReturn(repos);

        ReadRepositoriesRes res = readmeService.readRepositories("id");

        Assertions.assertEquals("name1", res.getRepositories().get(0).getName());
        Assertions.assertEquals("url1", res.getRepositories().get(0).getUrl());
        Assertions.assertEquals("name2", res.getRepositories().get(1).getName());
        Assertions.assertEquals("url2", res.getRepositories().get(1).getUrl());
    }
}
