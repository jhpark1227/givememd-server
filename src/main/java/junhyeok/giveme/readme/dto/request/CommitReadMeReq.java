package junhyeok.giveme.readme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor @AllArgsConstructor
public class CommitReadMeReq {
    private String repositoryName;
    private String content;
}
