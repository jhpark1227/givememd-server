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

        String accessToken = jwtUtils.createAccessToken(newUserProfile.getId());
        String refreshToken = jwtUtils.createRefreshToken(newUserProfile.getId());

        saveTokens(newUserProfile.getId(), githubToken, refreshToken);

        return new LoginRes(accessToken, refreshToken);
    }

    private void updateOrCreateUser(GithubProfile newUserProfile){
        Long id = newUserProfile.getId();
        userRepository.findById(id).ifPresentOrElse(
                        user -> user.changeProfile(newUserProfile),
                        () -> userRepository.save(new User(newUserProfile))
        );
    }

    private void saveTokens(Long userId, String githubToken, String refreshToken){
        githubTokenDao.save(userId, githubToken);
        refreshTokenDao.save(userId, refreshToken);
    }

    public ReissueRes reissue(String accessToken, String refreshToken){
        Long userId = jwtUtils.parseUserId(accessToken);

        String savedRefreshToken = refreshTokenDao.findById(userId);
        if(!savedRefreshToken.equals(refreshToken)){
            throw new RefreshTokenNotEqualsException();
        }
        String newAccessToken = jwtUtils.createAccessToken(userId);
        String newRefreshToken = jwtUtils.createRefreshToken(userId);

        updateRefreshToken(userId, newRefreshToken);

        return new ReissueRes(newAccessToken, newRefreshToken);
    }

    private void updateRefreshToken(Long userId, String refreshToken){
        refreshTokenDao.save(userId, refreshToken);
    }
}
