package junhyeok.giveme.readme.dto.response;

import junhyeok.giveme.readme.dto.request.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @AllArgsConstructor @NoArgsConstructor
public class ChatRes {
    List<Choice> choices;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Choice{
        private int index;
        private Message message;
    }
}
