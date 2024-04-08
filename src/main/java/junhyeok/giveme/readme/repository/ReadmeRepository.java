package junhyeok.giveme.readme.repository;

import junhyeok.giveme.readme.entity.Readme;
import junhyeok.giveme.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadmeRepository extends JpaRepository<Readme, Long> {
    Optional<Readme> findByNameAndUser(String name, User user);

    List<Readme> findByUser(User user);
}
