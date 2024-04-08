package junhyeok.giveme.user.controller;

import junhyeok.giveme.user.dto.ReissueReq;
import junhyeok.giveme.user.dto.response.LoginRes;
import junhyeok.giveme.user.dto.response.ReissueRes;
import junhyeok.giveme.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("api/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("login")
    public ResponseEntity<LoginRes> login(@RequestParam("code") String code){
        LoginRes res = authService.login(code);

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("reissue")
    public ReissueRes reissue(@RequestBody ReissueReq req){

        return authService.reissue(req.getAccessToken(), req.getRefreshToken());
    }
}
