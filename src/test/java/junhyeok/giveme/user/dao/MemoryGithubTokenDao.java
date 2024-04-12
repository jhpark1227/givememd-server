package junhyeok.giveme.user.dao;

import java.util.HashMap;
import java.util.Map;

public class MemoryGithubTokenDao implements GithubTokenDao {
    private final Map<Long,String> tokens = new HashMap<>();
    @Override
    public void save(Long id, String token) {
        tokens.put(id, token);
    }

    @Override
    public String findById(Long id) {
        return tokens.get(id);
    }

    @Override
    public void delete(Long id){
        tokens.remove(id);
    }
}
