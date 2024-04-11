package junhyeok.giveme.readme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class LoadFileInfoDto {
    private String accessToken;
    private String githubId;
    private String repositoryName;
    private String fileName;
}
