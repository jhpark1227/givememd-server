package junhyeok.giveme.test.exception;

import junhyeok.giveme.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class TestApplicationException extends ApplicationException {
    private static final String code = "400";
    private static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private static final String message = "잘못된 요청입니다.";

    public TestApplicationException(){
        this(code, httpStatus, message);
    }

    private TestApplicationException(String code, HttpStatus httpStatus, String message){
        super(code, httpStatus, message);
    }
}
