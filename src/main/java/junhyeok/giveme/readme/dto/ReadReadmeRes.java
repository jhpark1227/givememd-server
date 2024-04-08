package junhyeok.giveme.readme.dto;

import junhyeok.giveme.readme.entity.Readme;
import lombok.Getter;

@Getter
public class ReadReadmeRes {
    private Long id;
    private String name;
    private String content;

    private ReadReadmeRes(Long id, String name, String content){
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public static ReadReadmeRes toDto(Readme entity){
        return new ReadReadmeRes(entity.getId(), entity.getName(), entity.getContent());
    }
}
