package junhyeok.giveme.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GithubProfile {
    private Long id;

    private String name;

    @JsonProperty(value = "login")
    private String githubId;

    @JsonProperty(value = "avatar_url")
    private String image;
}
