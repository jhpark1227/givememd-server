package junhyeok.giveme.user.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisGithubTokenDao implements GithubTokenDao {
    private final ValueOperations<String, String> valueOperations;
    @Value("${jwt.valid-time.refresh}")
    private Long GITHUB_TOKEN_VALID_TIME;

    public RedisGithubTokenDao(StringRedisTemplate redisTemplate){
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void save(Long id, String token) {
        valueOperations.set("GT:"+id, token, Duration.ofMillis(GITHUB_TOKEN_VALID_TIME));
    }

    @Override
    public String findById(Long id) {
        return valueOperations.get("GT:"+id);
    }

    @Override
    public void delete(Long id){
        valueOperations.getAndDelete("GT:"+id);
    }
}
