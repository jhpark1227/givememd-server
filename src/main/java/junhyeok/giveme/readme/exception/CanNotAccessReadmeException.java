package junhyeok.giveme.readme.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CanNotAccessReadmeException extends ApplicationException {
    public CanNotAccessReadmeException(){
        super("400", HttpStatus.BAD_REQUEST, "해당 리드미에 접근할 수 없습니다.");
    }
}
