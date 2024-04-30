package junhyeok.giveme.user.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApplicationException {
    public InvalidTokenException(){
        super("400", HttpStatus.BAD_REQUEST, "잘못된 토큰입니다.");
    }
}
