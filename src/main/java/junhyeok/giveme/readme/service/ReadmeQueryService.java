package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.dto.ReadReadmeRes;
import junhyeok.giveme.readme.dto.response.ListReadmeRes;
import junhyeok.giveme.readme.entity.Readme;
import junhyeok.giveme.readme.exception.CanNotAccessReadmeException;
import junhyeok.giveme.readme.exception.NotExistReadmeException;
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
    public ListReadmeRes listReadmes(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotExistException::new);

        List<Readme> entities = readmeRepository.findByUser(user);

        return new ListReadmeRes(entities);
    }

    public ReadReadmeRes readReadme(Long userId, Long readmeId){
        if(!validateOwner(userId, readmeId)) throw new CanNotAccessReadmeException();

        Readme entity = readmeRepository.findById(readmeId)
                .orElseThrow(NotExistReadmeException::new);

        return ReadReadmeRes.toDto(entity);
    }

    public boolean validateOwner(Long userId, Long readmeId){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotExistException::new);

        Readme readme = readmeRepository.findById(readmeId)
                .orElseThrow(NotExistReadmeException::new);

        return user.equals(readme.getUser());
    }
}
