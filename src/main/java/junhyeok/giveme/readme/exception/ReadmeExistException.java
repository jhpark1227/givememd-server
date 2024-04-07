package junhyeok.giveme.readme.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ReadmeExistException extends ApplicationException {
    public ReadmeExistException(){
        super("400", HttpStatus.BAD_REQUEST, "리드미가 이미 존재합니다.");
    }
}
