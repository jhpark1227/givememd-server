package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.response.ListReadmeRes;
import junhyeok.giveme.readme.entity.Readme;
import junhyeok.giveme.readme.repository.ReadmeRepository;
import junhyeok.giveme.user.entity.User;
import junhyeok.giveme.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
@ExtendWith(MockitoExtension.class)
class ReadmeQueryServiceTest {
    private ReadmeQueryService readmeQueryService;

    @Mock
    ReadmeRepository readmeRepository;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setUp(){
        readmeQueryService = new ReadmeQueryService(readmeRepository, userRepository);
    }

    @Test
    void 리드미_목록_조회(){
        User user = User.builder()
                .githubId("user")
                .build();
        Readme readme1 = Readme.builder()
                        .name("repo1").build();
        Readme readme2 = Readme.builder()
                .name("repo2").build();
        given(userRepository.findByGithubId("user")).willReturn(Optional.of(user));
        given(readmeRepository.findByUser(user)).willReturn(List.of(readme1, readme2));

        ListReadmeRes res = readmeQueryService.listReadmes("user");

        Assertions.assertEquals("repo1", res.getRepos().get(0).getName());
        Assertions.assertEquals("repo2", res.getRepos().get(1).getName());
    }

}