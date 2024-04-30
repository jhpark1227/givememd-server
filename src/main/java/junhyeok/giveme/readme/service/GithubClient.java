package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.request.CommitFileReq;
import junhyeok.giveme.readme.dto.response.FileRes;
import junhyeok.giveme.readme.dto.CommitFileDto;
import junhyeok.giveme.readme.dto.LoadFileInfoDto;
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
                "https://api.github.com/user/repos?type=owner",
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

    public void commitFile(CommitFileDto req){

        CommitFileReq requestBody = CommitFileReq.builder()
                .message(req.getMessage())
                .author(req.getAuthor())
                .content(req.getContent())
                .sha(req.getSha()).build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+req.getAccessToken());
        HttpEntity request = new HttpEntity(requestBody, headers);

        String url = "https://api.github.com/repos/"+ req.getAuthor().getName()+"/"+req.getRepositoryName()+"/contents/"+req.getFileName();

        try{
            restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    Object.class
            );
        } catch (Exception e){
            throw new ExternalApiErrorException();
        }
    }

    public FileRes loadFileInfo(LoadFileInfoDto dto){
        String url = "https://api.github.com/repos/"+dto.getGithubId()+"/"+dto.getRepositoryName()+"/contents"+dto.getFileName();

        ResponseEntity<FileRes> res;
        try{
            res = restTemplate.getForEntity(url, FileRes.class);
        }catch (Exception e){
            return null;
        }
        return res.getBody();
    }
}
