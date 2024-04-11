package junhyeok.giveme.readme.dto.request;

import junhyeok.giveme.readme.dto.CommitFileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class CommitFileReq {
    private String message;
    private CommitFileDto.Author author;
    private String content;
    private String sha;
}
