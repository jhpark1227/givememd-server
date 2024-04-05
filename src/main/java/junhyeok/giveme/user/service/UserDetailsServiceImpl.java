package junhyeok.giveme.user.service;

import junhyeok.giveme.user.entity.User;
import junhyeok.giveme.user.exception.UserNotExistException;
import junhyeok.giveme.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByGithubId(username).orElseThrow(UserNotExistException::new);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getGithubId())
                .password("password")
                .roles(user.getRole().toString())
                .build();
    }
}
