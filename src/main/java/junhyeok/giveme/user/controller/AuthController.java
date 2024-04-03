package junhyeok.giveme.user.controller;

import junhyeok.giveme.user.dto.response.LoginRes;
import junhyeok.giveme.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("login")
    public ResponseEntity<LoginRes> login(@RequestParam("code") String code){
        LoginRes res = authService.login(code);

        return ResponseEntity.ok().body(res);
    }
}
