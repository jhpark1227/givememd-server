package junhyeok.giveme.readme.controller;

import junhyeok.giveme.global.config.SecurityConfig;
import junhyeok.giveme.global.security.JwtAuthenticationEntryPoint;
import junhyeok.giveme.global.security.JwtAuthenticationFilter;
import junhyeok.giveme.readme.dto.response.ReadRepositoriesRes;
import junhyeok.giveme.readme.dto.response.RepositoryInfo;
import junhyeok.giveme.readme.service.ReadmeService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ReadmeController.class},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
        })
public class ReadmeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadmeService readmeService;

    @Test @WithMockUser
    void 리포지토리_목록_조회() throws Exception{
        RepositoryInfo repo1 = new RepositoryInfo("name1", "url");
        ReadRepositoriesRes res = new ReadRepositoriesRes(new RepositoryInfo[]{repo1});
        BDDMockito.given(readmeService.readRepositories("user")).willReturn(res);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/readme/repos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
}
