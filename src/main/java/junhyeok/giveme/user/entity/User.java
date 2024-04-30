package junhyeok.giveme.user.entity;

import jakarta.persistence.*;
import junhyeok.giveme.user.dto.response.GithubProfile;
import junhyeok.giveme.user.enums.Role;
import lombok.*;

@Entity
@Getter
@Builder @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
public class User {
    @Id
    private Long id;

    private String githubId;

    private String email;

    private String name;

    private String image;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'USER'")
    private Role role;

    public User(GithubProfile profile, String email){
        this.id = profile.getId();
        this.githubId = profile.getGithubId();
        this.name = profile.getName();
        this.image = profile.getImage();
        this.role = Role.USER;
        this.email = email;
    }

    public void changeProfile(GithubProfile profile, String email){
        this.githubId = profile.getGithubId();
        this.name = profile.getName();
        this.image = profile.getImage();
        this.email = email;
    }
}
