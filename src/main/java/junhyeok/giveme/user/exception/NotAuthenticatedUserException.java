package junhyeok.giveme.user.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class NotAuthenticatedUserException extends ApplicationException {
    public NotAuthenticatedUserException(){
        super("401", HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
    }
}
