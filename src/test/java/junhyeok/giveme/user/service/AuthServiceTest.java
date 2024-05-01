package junhyeok.giveme.user.service;

import junhyeok.giveme.user.dao.GithubTokenDao;
import junhyeok.giveme.user.dao.MemoryGithubTokenDao;
import junhyeok.giveme.user.dao.MemoryRefreshTokenDao;
import junhyeok.giveme.user.dao.RefreshTokenDao;
import junhyeok.giveme.user.dto.response.GithubEmailRes;
import junhyeok.giveme.user.dto.response.GithubProfile;
import junhyeok.giveme.user.dto.response.LoginRes;
import junhyeok.giveme.user.entity.User;
import junhyeok.giveme.user.exception.RefreshTokenNotEqualsException;
import junhyeok.giveme.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import junhyeok.giveme.user.utils.JwtUtils;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private static final String OAUTH_CODE = "oauthCode";
    private static final String GITHUB_ACCESS_TOKEN = "githubAccessToken";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";


    private AuthService authService;

    @Mock
    private GithubOauthClient githubOauthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    private final GithubTokenDao githubTokenDao = new MemoryGithubTokenDao();
    private final RefreshTokenDao refreshTokenDao = new MemoryRefreshTokenDao();

    @BeforeEach
    void setUp(){
        authService = new AuthService(githubOauthClient, userRepository,jwtUtils,githubTokenDao,refreshTokenDao);
    }

    @Test
    void 첫_로그인_성공(){
        GithubProfile newProfile = new GithubProfile(1L,"name", "id","url");

        given(githubOauthClient.getGithubToken(anyString())).willReturn(GITHUB_ACCESS_TOKEN);
        given(githubOauthClient.getProfile(anyString())).willReturn(newProfile);
        GithubEmailRes emailInfo = new GithubEmailRes("email",true, true, "");
        given(githubOauthClient.loadGithubEmail(GITHUB_ACCESS_TOKEN)).willReturn(new GithubEmailRes[]{emailInfo});
        given(userRepository.findById(newProfile.getId())).willReturn(Optional.empty());
        given(jwtUtils.createAccessToken(newProfile.getId())).willReturn(ACCESS_TOKEN);
        given(jwtUtils.createRefreshToken()).willReturn(REFRESH_TOKEN);

        LoginRes res = authService.login(OAUTH_CODE);

        then(githubOauthClient).should().getGithubToken(anyString());
        then(githubOauthClient).should().getProfile(anyString());
        then(userRepository).should(times(1)).save(any(User.class));

        String savedGithubToken = githubTokenDao.findById(newProfile.getId());
        String savedRefreshToken = refreshTokenDao.findById(newProfile.getId());
        Assertions.assertEquals(GITHUB_ACCESS_TOKEN, savedGithubToken);
        Assertions.assertEquals(REFRESH_TOKEN, savedRefreshToken);
        Assertions.assertEquals(REFRESH_TOKEN, res.getRefreshToken());
        Assertions.assertEquals(ACCESS_TOKEN, res.getAccessToken());
    }

    @Test
    void 이미_존재하는_회원_로그인(){
        GithubProfile newProfile = new GithubProfile(1L,"name", "id","url");
        User user = User.builder().build();

        given(githubOauthClient.getGithubToken(anyString())).willReturn(GITHUB_ACCESS_TOKEN);
        given(githubOauthClient.getProfile(anyString())).willReturn(newProfile);
        GithubEmailRes emailInfo = new GithubEmailRes("email",true, true, "");
        given(githubOauthClient.loadGithubEmail(GITHUB_ACCESS_TOKEN)).willReturn(new GithubEmailRes[]{emailInfo});
        given(userRepository.findById(newProfile.getId())).willReturn(Optional.of(user));
        given(jwtUtils.createAccessToken(newProfile.getId())).willReturn(ACCESS_TOKEN);
        given(jwtUtils.createRefreshToken()).willReturn(REFRESH_TOKEN);

        LoginRes res = authService.login(OAUTH_CODE);

        then(githubOauthClient).should().getGithubToken(anyString());
        then(githubOauthClient).should().getProfile(anyString());
        then(userRepository).should(never()).save(any());

        String savedGithubToken = githubTokenDao.findById(newProfile.getId());
        String savedRefreshToken = refreshTokenDao.findById(newProfile.getId());
        Assertions.assertEquals(GITHUB_ACCESS_TOKEN, savedGithubToken);
        Assertions.assertEquals(REFRESH_TOKEN, savedRefreshToken);
        Assertions.assertEquals(REFRESH_TOKEN, res.getRefreshToken());
        Assertions.assertEquals(ACCESS_TOKEN, res.getAccessToken());
    }

    @Test
    void 토큰_갱신(){
        String userId = "user";
        refreshTokenDao.save(1L, "refreshToken");
        given(jwtUtils.parseUserId("accessToken")).willReturn(1L);
        given(jwtUtils.createAccessToken(anyLong())).willReturn("newAccessToken");
        given(jwtUtils.createRefreshToken()).willReturn("newRefreshToken");

        authService.reissue("accessToken", "refreshToken");

        then(jwtUtils).should().createAccessToken(1L);
        Assertions.assertEquals("newRefreshToken", refreshTokenDao.findById(1L));
    }

    @Test
    void 리프레시토큰_불일치로_토큰_갱신_실패(){
        Long userId = 1L;
        refreshTokenDao.save(userId, "refreshToken");
        given(jwtUtils.parseUserId("accessToken")).willReturn(1L);

        assertThrows(RefreshTokenNotEqualsException.class,()->{
            authService.reissue("accessToken", "badRefreshToken");
        });
    }
}
