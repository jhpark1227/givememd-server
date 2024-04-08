package junhyeok.giveme.user.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import junhyeok.giveme.global.exception.ApplicationException;
import junhyeok.giveme.user.exception.AuthExceptions;
import junhyeok.giveme.user.exception.InvalidTokenException;
import junhyeok.giveme.user.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private final Key key;
    private final Long ACCESS_TOKEN_VALID_TIME;
    private final Long REFRESH_TOKEN_VALID_TIME;
    private final UserDetailsServiceImpl userDetailsService;
    public JwtUtils(
            @Value("${jwt.key}") String secretKey,
            @Value("${jwt.valid-time.access}") Long accessTokenValidTime,
            @Value("${jwt.valid-time.refresh}") Long refreshTokenValidTime,
            UserDetailsServiceImpl userDetailsService
    ){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.ACCESS_TOKEN_VALID_TIME = accessTokenValidTime;
        this.REFRESH_TOKEN_VALID_TIME = refreshTokenValidTime;
        this.userDetailsService = userDetailsService;
    }

    public String createAccessToken(String payload){
        Date now = new Date();
        return Jwts.builder()
                .claim("id", payload)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ACCESS_TOKEN_VALID_TIME))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String payload){
        Date now = new Date();
        return Jwts.builder()
                .claim("id", payload)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+REFRESH_TOKEN_VALID_TIME))
                .signWith(key)
                .compact();
    }

    public String validToken(HttpServletRequest request, String token) {
        try {
            String userId =
                    Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody().get("id").toString();

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

    public Authentication getAuthentication(String userId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public String parseUserId(String token){
        String userId;
        try{
            userId =  Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("id").toString();
        } catch (ExpiredJwtException e){
            userId = e.getClaims().get("id").toString();
        } catch (Exception e){
            throw new InvalidTokenException();
        }

        return userId;
    }
}
