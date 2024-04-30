package junhyeok.giveme.user.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtUtilsTest {
    @Autowired
    private JwtUtils jwtUtils;
    @Test
    void 토큰_생성(){
        String accessToken = jwtUtils.createAccessToken(1L);
        String refreshToken = jwtUtils.createRefreshToken(1L);
    }

}
