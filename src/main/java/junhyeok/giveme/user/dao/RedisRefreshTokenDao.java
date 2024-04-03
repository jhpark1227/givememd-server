package junhyeok.giveme.user.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisRefreshTokenDao implements RefreshTokenDao {
    private final ValueOperations<String, String> valueOperations;
    @Value("${jwt.valid-time.refresh}")
    private Long REFRESH_TOKEN_VALID_TIME;

    public RedisRefreshTokenDao(StringRedisTemplate redisTemplate){
        this.valueOperations = redisTemplate.opsForValue();
    }
    @Override
    public void save(String githubId, String token) {
        valueOperations.set("RT:"+githubId, token, Duration.ofMillis(REFRESH_TOKEN_VALID_TIME));
    }

    @Override
    public String findByGithubId(String githubId) {
        return null;
    }
}
