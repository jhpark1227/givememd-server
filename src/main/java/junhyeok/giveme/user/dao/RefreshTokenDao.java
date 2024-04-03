package junhyeok.giveme.user.dao;

import org.springframework.stereotype.Component;

public interface RefreshTokenDao {
    void save(String githubId, String token);

    String findByGithubId(String githubId);
}
