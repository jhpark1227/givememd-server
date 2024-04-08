package junhyeok.giveme.readme.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class NotExistReadmeException extends ApplicationException {
    public NotExistReadmeException(){
        super("404", HttpStatus.NOT_FOUND,"리드미가 존재하지 않습니다.");
    }
}
