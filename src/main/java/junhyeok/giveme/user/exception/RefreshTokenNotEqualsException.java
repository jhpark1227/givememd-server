package junhyeok.giveme.user.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class RefreshTokenNotEqualsException extends ApplicationException {
    public RefreshTokenNotEqualsException(){
        super("400", HttpStatus.BAD_REQUEST, "리프레시토큰이 유효하지 않습니다.");
    }
}
