package junhyeok.giveme.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor @Getter
public enum AuthExceptions {
    TOKEN_EXPIRED("401", HttpStatus.UNAUTHORIZED,"토큰이 만료되었습니다."),
    INVALID_TOKEN("401",HttpStatus.UNAUTHORIZED,"잘못된 토큰입니다."),
    FAIL_AUTHENTICATION("401", HttpStatus.UNAUTHORIZED,"인증에 실패하였습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
