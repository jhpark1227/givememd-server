package junhyeok.giveme.user.controller;

import junhyeok.giveme.user.dto.response.LoginRes;
import junhyeok.giveme.user.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AuthControllerTest {
    private static final String OAUTH_CODE = "oauthCode";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser
    void 로그인() throws Exception {
        LoginRes res = new LoginRes(ACCESS_TOKEN, REFRESH_TOKEN);
        BDDMockito.given(authService.login(BDDMockito.anyString())).willReturn(res);

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/login?code="+OAUTH_CODE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("refreshToken").value(REFRESH_TOKEN));
    }
}