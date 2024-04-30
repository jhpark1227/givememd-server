package junhyeok.giveme.user.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ExternalApiErrorException extends ApplicationException {
    public ExternalApiErrorException(){
        super("500", HttpStatus.BAD_REQUEST, "외부API 통신에 실패했습니다.");
    }
}
