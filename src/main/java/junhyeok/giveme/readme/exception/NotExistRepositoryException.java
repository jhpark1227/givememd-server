package junhyeok.giveme.readme.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class NotExistRepositoryException extends ApplicationException {
    public NotExistRepositoryException(){
        super("404", HttpStatus.NOT_FOUND, "리포지토리가 존재하지 않습니다.");
    }
}
