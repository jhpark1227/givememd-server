package junhyeok.giveme.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GithubTokenReq {
    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    String clientSecret;

    @JsonProperty("code")
    String code;

    public GithubTokenReq(String code, String clientId, String clientSecret){
        this.code = code;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
}
