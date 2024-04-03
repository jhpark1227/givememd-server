package junhyeok.giveme.user.dao;

public interface GithubTokenDao {
    void save(String githubId, String token);

    String findByGithubId(String githubId);
}
