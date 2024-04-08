package junhyeok.giveme.user.dao;

public interface GithubTokenDao {
    void save(Long id, String token);

    String findById(Long id);
}
