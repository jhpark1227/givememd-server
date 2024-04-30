package junhyeok.giveme.global.exception;

import junhyeok.giveme.global.apiPayload.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> applicationException(ApplicationException exception){
        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(ApiErrorResponse.onFailure(exception.getCode(), exception.getMessage()));
    }
}
