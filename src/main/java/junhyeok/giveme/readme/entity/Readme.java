package junhyeok.giveme.readme.entity;

import jakarta.persistence.*;
import junhyeok.giveme.user.entity.User;
import lombok.*;

@Entity
@Getter
@Builder @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
public class Readme {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String name;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    public void changeContent(String content){
        this.content = content;
    }
}
