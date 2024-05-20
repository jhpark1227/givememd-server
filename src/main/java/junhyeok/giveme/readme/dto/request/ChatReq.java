package junhyeok.giveme.readme.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ChatReq {
    String model;
    List<Message> messages;
    @JsonProperty("max_tokens")
    int maxTokens;
    double temperature;
}