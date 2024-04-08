package junhyeok.giveme.readme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class UpdateReadmeReq {
    private Long readmeId;
    private String content;
}
