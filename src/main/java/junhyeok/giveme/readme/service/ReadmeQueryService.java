package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.response.ListReadmeRes;
import junhyeok.giveme.readme.entity.Readme;
import junhyeok.giveme.readme.repository.ReadmeRepository;
import junhyeok.giveme.user.entity.User;
import junhyeok.giveme.user.exception.UserNotExistException;
import junhyeok.giveme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadmeQueryService {
    private final ReadmeRepository readmeRepository;
    private final UserRepository userRepository;
    public ListReadmeRes listReadmes(String userId){
        User user = userRepository.findByGithubId(userId)
                .orElseThrow(UserNotExistException::new);

        List<Readme> entities = readmeRepository.findByUser(user);

        return new ListReadmeRes(entities);
    }
}
