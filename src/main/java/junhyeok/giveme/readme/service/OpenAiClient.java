package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.request.ChatReq;
import junhyeok.giveme.readme.dto.request.Message;
import junhyeok.giveme.readme.dto.response.ChatRes;
import junhyeok.giveme.user.exception.ExternalApiErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OpenAiClient {
    private final RestTemplate restTemplate;
    private final String model;
    private final String token;

    public OpenAiClient(
            RestTemplate restTemplate,
            @Value("${chat-gpt.model}") String model,
            @Value("${chat-gpt.token}") String token
    ){
        this.restTemplate = restTemplate;
        this.model = model;
        this.token = token;
    }

    public String sendMessage(List<Message> messages){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+token);
        HttpEntity entity = new HttpEntity(new ChatReq(model, messages), headers);

        try{
            ChatRes res = restTemplate.postForObject(
                    "https://api.openai.com/v1/chat/completions",
                    entity,
                    ChatRes.class
            );

            return res.getChoices().get(0).getMessage().getContent();
        } catch (Exception e){
            throw new ExternalApiErrorException();
        }
    }
}
