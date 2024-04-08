package junhyeok.giveme.readme.controller;

import junhyeok.giveme.global.security.UserDetailsImpl;
import junhyeok.giveme.readme.dto.ReadReadmeRes;
import junhyeok.giveme.readme.dto.request.CreateReadmeReq;
import junhyeok.giveme.readme.dto.request.SaveReadmeReq;
import junhyeok.giveme.readme.dto.request.UpdateReadmeReq;
import junhyeok.giveme.readme.dto.response.CreateReadmeRes;
import junhyeok.giveme.readme.dto.response.ListReadmeRes;
import junhyeok.giveme.readme.dto.response.ReadRepositoriesRes;
import junhyeok.giveme.readme.service.ReadmeQueryService;
import junhyeok.giveme.readme.service.ReadmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController @RequestMapping("/api/readme")
@RequiredArgsConstructor
public class ReadmeController {
    private final ReadmeService readmeService;
    private final ReadmeQueryService readmeQueryService;
    @GetMapping("repos")
    public ResponseEntity<ReadRepositoriesRes> readRepositories(Authentication auth){
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        ReadRepositoriesRes res = readmeService.readRepositories(user.getId());

        return ResponseEntity.ok().body(res);
    }

    @PostMapping
    public ResponseEntity<CreateReadmeRes> createReadme(
            Authentication auth, @RequestBody CreateReadmeReq req){
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Long userId = user.getId();
        String url = req.getUrl();

        CreateReadmeRes res = readmeService.createReadme(userId, url);

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("save")
    public ResponseEntity saveReadme(Authentication auth, @RequestBody SaveReadmeReq req){
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        readmeService.saveReadme(user.getId(), req);

        return ResponseEntity.created(null).build();
    }

    @GetMapping("list")
    public ListReadmeRes listReadmes(Authentication auth){
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        return readmeQueryService.listReadmes(user.getId());
    }

    @GetMapping("/{readmeId}")
    public ReadReadmeRes readReadme(Authentication auth, @PathVariable("readmeId") Long readmeId){
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        return readmeQueryService.readReadme(user.getId(), readmeId);
    }

    @PatchMapping("/{readmeId}")
    public ResponseEntity updateReadme(Authentication auth,@PathVariable("readmeId") Long readmeId, @RequestBody UpdateReadmeReq req){
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        readmeService.updateReadme(user.getId(), readmeId, req.getContent());

        return ResponseEntity.noContent().build();
    }
}
