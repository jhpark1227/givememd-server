package junhyeok.giveme.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueRes {
    private String accessToken;
    private String refreshToken;
}
