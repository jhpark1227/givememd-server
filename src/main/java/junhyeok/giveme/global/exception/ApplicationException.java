package junhyeok.giveme.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException{
    private String code;
    private HttpStatus httpStatus;
    private String message;

    public ApplicationException(String code, HttpStatus status, String message){
           this.code = code;
           this.httpStatus = status;
           this.message = message;
    }
}
