package junhyeok.giveme.user.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import junhyeok.giveme.user.exception.AuthExceptions;
import junhyeok.giveme.user.exception.InvalidTokenException;
import junhyeok.giveme.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private final Key key;
    private final Long ACCESS_TOKEN_VALID_TIME;
    private final Long REFRESH_TOKEN_VALID_TIME;
    private final UserRepository userRepository;
    public JwtUtils(
            @Value("${jwt.key}") String secretKey,
            @Value("${jwt.valid-time.access}") Long accessTokenValidTime,
            @Value("${jwt.valid-time.refresh}") Long refreshTokenValidTime,
            UserRepository userRepository
    ){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.ACCESS_TOKEN_VALID_TIME = accessTokenValidTime;
        this.REFRESH_TOKEN_VALID_TIME = refreshTokenValidTime;
        this.userRepository = userRepository;
    }

    public String createAccessToken(Long payload){
        Date now = new Date();
        return Jwts.builder()
                .claim("id", payload)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ACCESS_TOKEN_VALID_TIME))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long payload){
        Date now = new Date();
        return Jwts.builder()
                .claim("id", payload)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+REFRESH_TOKEN_VALID_TIME))
                .signWith(key)
                .compact();
    }

    public Long validToken(HttpServletRequest request, String token) {
        try {
            Long userId =
                    Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody().get("id", Long.class);

            return userId;
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", AuthExceptions.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e){
            request.setAttribute("exception", AuthExceptions.INVALID_TOKEN);
        } catch (Exception e) {
            request.setAttribute("exception", AuthExceptions.FAIL_AUTHENTICATION);
        }
        return null;
    }

    public Long parseUserId(String token){
        Long userId;
        try{
            userId =  Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("id",Long.class);
        } catch (ExpiredJwtException e){
            userId = e.getClaims().get("id",Long.class);
        } catch (Exception e){
            throw new InvalidTokenException();
        }

        return userId;
    }
}
