package junhyeok.giveme.readme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class CommitFileDto {
    private String accessToken;
    private String repositoryName;
    private String fileName;
    private String message;
    private Author author;
    private String content;
    private String sha;

    @Getter
    @AllArgsConstructor
    public static class Author{
        private String name;
        private String email;
    }

    public void addSha(String sha){
        this.sha = sha;
    }
}
