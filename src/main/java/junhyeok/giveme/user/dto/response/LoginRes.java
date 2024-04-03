package junhyeok.giveme.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRes {
    private String accessToken;
    private String refreshToken;
}
