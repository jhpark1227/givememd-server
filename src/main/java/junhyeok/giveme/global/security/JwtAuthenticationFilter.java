package junhyeok.giveme.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import junhyeok.giveme.user.entity.User;
import junhyeok.giveme.user.exception.UserNotExistException;
import junhyeok.giveme.user.repository.UserRepository;
import junhyeok.giveme.user.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request.getHeader("Authorization"));

        Long userId = jwtUtils.validToken(request, token);
        if(userId != null){
            SecurityContextHolder.getContext().setAuthentication(getAuthentication(userId));
        }

        filterChain.doFilter(request,response);
    }

    private String resolveToken(String token){
        if (token != null) {
            return token.substring("Bearer ".length());
        } else {
            return "";
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotExistException::new);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                                        .id(user.getId())
                                        .role(user.getRole()).build();

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}