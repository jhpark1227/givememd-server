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
    public void save(Long id, String token) {
        valueOperations.set("RT:"+id, token, Duration.ofMillis(REFRESH_TOKEN_VALID_TIME));
    }

    @Override
    public String findById(Long id) {
        return valueOperations.get("RT:"+id);
    }
}
