package junhyeok.giveme.user.repository;

import junhyeok.giveme.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
}
