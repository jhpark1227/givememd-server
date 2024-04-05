package junhyeok.giveme.readme.service;

import junhyeok.giveme.user.dao.GithubTokenDao;
import junhyeok.giveme.readme.dto.response.ReadRepositoriesRes;
import junhyeok.giveme.readme.dto.response.RepositoryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadmeService {
    private final GithubClient githubClient;
    private final GithubTokenDao githubTokenDao;

    public ReadRepositoriesRes readRepositories(String userId){
        String githubToken = githubTokenDao.findByGithubId(userId);

        RepositoryInfo[] repos = githubClient.findRepositories(githubToken);

        return new ReadRepositoriesRes(repos);
    }
}
