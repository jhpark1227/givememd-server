package junhyeok.giveme.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @AllArgsConstructor @NoArgsConstructor
public class GithubTokenRes {
    @JsonProperty("access_token")
    private String token;

    private String error;
}
