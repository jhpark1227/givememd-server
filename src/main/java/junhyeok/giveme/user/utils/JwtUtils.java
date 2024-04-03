package junhyeok.giveme.user.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private final String SECRET_KEY;
    private final Long ACCESS_TOKEN_VALID_TIME;
    private final Long REFRESH_TOKEN_VALID_TIME;
    public JwtUtils(
            @Value("${jwt.key}") String secretKey,
            @Value("${jwt.valid-time.access}") Long accessTokenValidTime,
            @Value("${jwt.valid-time.refresh}") Long refreshTokenValidTime
    ){
        this.SECRET_KEY = secretKey;
        this.ACCESS_TOKEN_VALID_TIME = accessTokenValidTime;
        this.REFRESH_TOKEN_VALID_TIME = refreshTokenValidTime;
    }

    public String createAccessToken(String payload){
        Date now = new Date();
        return Jwts.builder()
                .claim("id", payload)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ACCESS_TOKEN_VALID_TIME))
                .signWith(getSigningkey(SECRET_KEY))
                .compact();
    }

    public String createRefreshToken(String payload){
        Date now = new Date();
        return Jwts.builder()
                .claim("id", payload)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+REFRESH_TOKEN_VALID_TIME))
                .signWith(getSigningkey(SECRET_KEY))
                .compact();
    }

    private Key getSigningkey(String secretKey){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}
