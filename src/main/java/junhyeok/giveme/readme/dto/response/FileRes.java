package junhyeok.giveme.readme.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileRes {
    private String name;
    private String type;
    @JsonProperty("download_url")
    private String downloadUrl;
    private String sha;
}
