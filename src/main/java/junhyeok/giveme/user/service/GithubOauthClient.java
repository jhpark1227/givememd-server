package junhyeok.giveme.user.service;

import junhyeok.giveme.user.dto.request.GithubTokenReq;
import junhyeok.giveme.user.dto.response.GithubProfile;
import junhyeok.giveme.user.dto.response.GithubTokenRes;
import junhyeok.giveme.user.exception.ExternalApiErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubOauthClient {
    private final RestTemplate restTemplate;

    public GithubOauthClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Value("${oauth.client.id}")
    private String clientId;

    @Value("${oauth.client.secret}")
    private String clientSecret;

    public String getGitHubToken(String code){
        GithubTokenReq req = new GithubTokenReq(code, clientId, clientSecret);

        GithubTokenRes response = restTemplate.postForObject(
                "https://github.com/login/oauth/access_token",
                req,
                GithubTokenRes.class
        );

        if(response.getError()!=null){
            throw new ExternalApiErrorException();
        }

        return response.getToken();
    }
}
