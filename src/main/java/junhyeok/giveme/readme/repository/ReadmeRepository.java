package junhyeok.giveme.readme.repository;

import junhyeok.giveme.readme.entity.Readme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadmeRepository extends JpaRepository<Readme, Long> {
}
