package junhyeok.giveme.readme.dto.response;

import junhyeok.giveme.readme.entity.Readme;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ListReadmeRes {
    private List<ReadmeInfo> repos;

    public ListReadmeRes(List<Readme> entities){
        repos = entities.stream().map(entity-> new ReadmeInfo(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    public static class ReadmeInfo{
        private Long readmeId;
        private String name;
    }
}
