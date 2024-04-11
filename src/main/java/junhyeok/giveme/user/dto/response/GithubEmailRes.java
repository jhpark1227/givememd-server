package junhyeok.giveme.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor @AllArgsConstructor
public class GithubEmailRes {
    private String email;
    private boolean primary;
    private boolean verified;
    private String visibility;
}
