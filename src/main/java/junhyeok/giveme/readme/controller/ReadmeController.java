package junhyeok.giveme.readme.controller;

import junhyeok.giveme.readme.dto.request.CreateReadmeReq;
import junhyeok.giveme.readme.dto.request.SaveReadmeReq;
import junhyeok.giveme.readme.dto.response.CreateReadmeRes;
import junhyeok.giveme.readme.dto.response.ReadRepositoriesRes;
import junhyeok.giveme.readme.service.ReadmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/readme")
@RequiredArgsConstructor
public class ReadmeController {
    private final ReadmeService readmeService;
    @GetMapping("repos")
    public ResponseEntity<ReadRepositoriesRes> readRepositories(Authentication auth){
        String userId = auth.getName();

        ReadRepositoriesRes res = readmeService.readRepositories(userId);

        return ResponseEntity.ok().body(res);
    }

    @PostMapping
    public ResponseEntity<CreateReadmeRes> createReadme(
            Authentication auth, @RequestBody CreateReadmeReq req){
        String userId = auth.getName();
        String url = req.getUrl();

        CreateReadmeRes res = readmeService.createReadme(userId, url);

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("save")
    public ResponseEntity saveReadme(Authentication auth, @RequestBody SaveReadmeReq req){
        String userId = auth.getName();

        readmeService.saveReadme(userId, req);

        return ResponseEntity.created(null).build();
    }

}
