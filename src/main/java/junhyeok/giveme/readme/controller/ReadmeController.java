package junhyeok.giveme.readme.controller;

import junhyeok.giveme.readme.dto.response.ReadRepositoriesRes;
import junhyeok.giveme.readme.service.ReadmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
