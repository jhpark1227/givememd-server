package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.response.FileRes;
import junhyeok.giveme.user.exception.ExternalApiErrorException;
import junhyeok.giveme.readme.dto.response.RepositoryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class GithubClient {
    private final RestTemplate restTemplate;

    public RepositoryInfo[] findRepositories(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+token);

        HttpEntity entity = new HttpEntity(null, headers);

        ResponseEntity<RepositoryInfo[]> responseEntity = restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.GET,
                entity,
                RepositoryInfo[].class
        );
        if(responseEntity.getStatusCode()!= HttpStatus.OK){
            throw new ExternalApiErrorException();
        }

        return responseEntity.getBody();
    }

    public FileRes[] readDirectory(String token, String url){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<FileRes[]> res = restTemplate.exchange(url,
                HttpMethod.GET,
                request,
                FileRes[].class
        );

        if(res.getStatusCode()!=HttpStatus.OK){
            throw new ExternalApiErrorException();
        }

        return res.getBody();
    }

    public String readFile(String accessToken, String url){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+accessToken);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> res = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        if(res.getStatusCode()!=HttpStatus.OK){
            throw new ExternalApiErrorException();
        }

        return res.getBody();
    }
}
