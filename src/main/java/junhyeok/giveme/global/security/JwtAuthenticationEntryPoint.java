package junhyeok.giveme.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import junhyeok.giveme.global.apiPayload.ApiErrorResponse;
import junhyeok.giveme.user.exception.AuthExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        AuthExceptions exception = (AuthExceptions) request.getAttribute("exception");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(exception.getStatus().value());
        response.setCharacterEncoding("UTF-8");

        ApiErrorResponse result = ApiErrorResponse.onFailure(exception.getCode(), exception.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
