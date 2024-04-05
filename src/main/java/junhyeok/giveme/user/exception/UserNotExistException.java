package junhyeok.giveme.user.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserNotExistException extends ApplicationException {
    public UserNotExistException(){
        super("500", HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다.");
    }
}
