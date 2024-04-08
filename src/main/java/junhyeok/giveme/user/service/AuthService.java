package junhyeok.giveme.user.service;

import junhyeok.giveme.user.dao.GithubTokenDao;
import junhyeok.giveme.user.dao.RefreshTokenDao;
import junhyeok.giveme.user.dto.response.LoginRes;
import junhyeok.giveme.user.dto.response.ReissueRes;
import junhyeok.giveme.user.exception.RefreshTokenNotEqualsException;
import junhyeok.giveme.user.utils.JwtUtils;
import junhyeok.giveme.user.dto.response.GithubProfile;
import junhyeok.giveme.user.entity.User;
import junhyeok.giveme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {
    private final GithubOauthClient oauthClient;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final GithubTokenDao githubTokenDao;
    private final RefreshTokenDao refreshTokenDao;

    public LoginRes login(String code){
        String githubToken = oauthClient.getGithubToken(code);

        GithubProfile newUserProfile = oauthClient.getProfile(githubToken);

        updateOrCreateUser(newUserProfile);

        String accessToken = jwtUtils.createAccessToken(newUserProfile.getGithubId());
        String refreshToken = jwtUtils.createRefreshToken(newUserProfile.getGithubId());

        saveTokens(newUserProfile.getGithubId(), githubToken, refreshToken);

        return new LoginRes(accessToken, refreshToken);
    }

    private void updateOrCreateUser(GithubProfile newUserProfile){
        Long id = newUserProfile.getId();
        userRepository.findById(id).ifPresentOrElse(
                        user -> user.changeProfile(newUserProfile),
                        () -> userRepository.save(new User(newUserProfile))
        );
    }

    private void saveTokens(String githubId, String githubToken, String refreshToken){
        githubTokenDao.save(githubId, githubToken);
        refreshTokenDao.save(githubId, refreshToken);
    }

    public ReissueRes reissue(String accessToken, String refreshToken){
        String userId = jwtUtils.parseUserId(accessToken);

        String savedRefreshToken = refreshTokenDao.findByGithubId(userId);
        if(!savedRefreshToken.equals(refreshToken)){
            throw new RefreshTokenNotEqualsException();
        }
        String newAccessToken = jwtUtils.createAccessToken(userId);
        String newRefreshToken = jwtUtils.createRefreshToken(userId);

        return new ReissueRes(newAccessToken, newRefreshToken);
    }
}
