package junhyeok.giveme.global.apiPayload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiErrorResponse {
    private String errorCode;
    private String message;

    public static ApiErrorResponse onFailure(String errorCode, String message){
        return new ApiErrorResponse(errorCode, message);
    }
}