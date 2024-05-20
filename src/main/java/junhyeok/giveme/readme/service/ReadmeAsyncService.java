package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.request.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadmeAsyncService {
    private final GithubClient githubClient;
    private final OpenAiClient openAiClient;
    @Async
    public CompletableFuture<String> summarizeFile(String token, String url){
        String file = githubClient.readFile(token, url);
        List<Message> messages = new ArrayList<>();

        messages.add(new Message("user", "이 파일의 핵심만 추출하라: "+file));

        String res = openAiClient.sendMessage(messages);

        return CompletableFuture.completedFuture(res);
    }
}
