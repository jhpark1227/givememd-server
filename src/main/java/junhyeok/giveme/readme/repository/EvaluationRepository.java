package junhyeok.giveme.readme.repository;

import junhyeok.giveme.readme.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
