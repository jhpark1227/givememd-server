package junhyeok.giveme.readme.service;

import junhyeok.giveme.readme.entity.Evaluation;
import junhyeok.giveme.readme.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service @RequiredArgsConstructor
public class EvaluationService {
    public final EvaluationRepository evaluationRepository;
    public double evaluateReadme(String content){
        double score = 0;
        int wordCount = content.split(" ").length;

        boolean hasTitleSection = content.contains("프로젝트");
        boolean hasInstallationSection = content.contains("설치 방법");
        boolean hasUsageExampleSection = content.contains("사용법");
        String[] keyword = {"기술 스택", "개발 기간", "팀원", "핵심기능"};
        boolean hasContributionSection = Arrays.stream(keyword).anyMatch(content::contains);
        boolean hasCodeExample = content.contains("```");
        boolean hasLicense = content.contains("라이선스");
        boolean hasImagesOrLogos = content.contains("![image]") || content.contains("![logo]");

        score += hasTitleSection ? 10 : 0;
        score += hasInstallationSection ? 20 : 0;
        score += hasUsageExampleSection ? 20 : 0;
        score += hasContributionSection ? 10 : 0;
        score += hasCodeExample ? 10 : 0;
        score += hasLicense ? 5 : 0;
        score += hasImagesOrLogos ? 5 : 0;

        int totalLines = content.split("\n").length;
        int commentLines = content.split("#").length;
        double commentRatio = (double)commentLines / totalLines;
        double documentation_score = Math.min(10, commentRatio * 20);
        score += documentation_score;

        if (300 <= wordCount && wordCount <= 1000){
            score += 10;
        }

        return score;
    }

    void saveEvaluation(String url, String content){
        double score = evaluateReadme(content);

        evaluationRepository.save(
                Evaluation.builder()
                        .url(url)
                        .content(content)
                        .score(score).build()
        );
    }
}
