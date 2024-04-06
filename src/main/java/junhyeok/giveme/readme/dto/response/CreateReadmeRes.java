package junhyeok.giveme.readme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateReadmeRes {
    private String name;
    private String content;
}
