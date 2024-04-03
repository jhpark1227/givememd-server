package junhyeok.giveme.user.dao;

import java.util.HashMap;
import java.util.Map;

public class MemoryRefreshTokenDao implements RefreshTokenDao {
    private final Map<String,String> tokens = new HashMap<>();
    @Override
    public void save(String githubId, String token) {
        tokens.put(githubId, token);
    }

    @Override
    public String findByGithubId(String githubId) {
        return tokens.get(githubId);
    }
}
