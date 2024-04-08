package junhyeok.giveme.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueReq {
    private String accessToken;
    private String refreshToken;
}
