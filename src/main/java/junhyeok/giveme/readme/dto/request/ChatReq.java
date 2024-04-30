package junhyeok.giveme.readme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ChatReq {
    String model;
    List<Message> messages;
}