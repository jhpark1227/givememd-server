package junhyeok.giveme.test.controller;

import junhyeok.giveme.global.apiPayload.ApiErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class TestControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Test
    void 테스트_api_응답_확인(){
        String res = restTemplate.getForObject("/test", String.class);

        assertEquals("Hello", res);
    }

    @Test
    void 테스트_api_익셉션_발생_확인(){
        ResponseEntity<ApiErrorResponse> res =
                restTemplate.getForEntity("/test/exception", ApiErrorResponse.class);

        assertEquals("400", res.getBody().getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("잘못된 요청입니다.", res.getBody().getMessage());
    }
}
