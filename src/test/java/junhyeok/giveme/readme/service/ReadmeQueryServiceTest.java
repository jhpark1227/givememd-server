package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.ReadReadmeRes;
import junhyeok.giveme.readme.dto.response.ListReadmeRes;
import junhyeok.giveme.readme.entity.Readme;
import junhyeok.giveme.readme.exception.CanNotAccessReadmeException;
import junhyeok.giveme.readme.exception.NotExistReadmeException;
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
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(readmeRepository.findByUser(user)).willReturn(List.of(readme1, readme2));

        ListReadmeRes res = readmeQueryService.listReadmes(1L);

        Assertions.assertEquals("repo1", res.getRepos().get(0).getName());
        Assertions.assertEquals("repo2", res.getRepos().get(1).getName());
    }

    @Test
    void 리드미_조회(){
        User user = User.builder().id(1L).githubId("user").build();
        Readme readme = Readme.builder().id(1L).user(user).build();
        given(readmeRepository.findById(1L)).willReturn(Optional.of(readme));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        ReadReadmeRes res = readmeQueryService.readReadme(1L, 1L);

        Assertions.assertEquals(1L, res.getId());
    }

    @Test
    void 다른_사용자의_리드미_조회(){
        User otherUser = User.builder().id(1L).build();
        User user = User.builder().id(2L).build();
        Readme readme = Readme.builder().id(1L).user(user).build();
        given(readmeRepository.findById(1L)).willReturn(Optional.of(readme));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));

        Assertions.assertThrows(CanNotAccessReadmeException.class,()->{
           readmeQueryService.readReadme(2L, 1L);
        });
    }

    @Test
    void 존재하지_않는_리드미ID로_조회(){
        User user = User.builder().id(1L).build();
        given(readmeRepository.findById(1L)).willReturn(Optional.empty());
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        Assertions.assertThrows(NotExistReadmeException.class,()->{
            readmeQueryService.readReadme(1L, 1L);
        });
    }

}