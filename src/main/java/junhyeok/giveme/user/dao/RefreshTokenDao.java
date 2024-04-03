package junhyeok.giveme.user.dao;


public interface RefreshTokenDao {
    void save(String githubId, String token);

    String findByGithubId(String githubId);
}
