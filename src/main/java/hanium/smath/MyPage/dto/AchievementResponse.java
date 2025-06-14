package hanium.smath.MyPage.dto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AchievementResponse {
    private int consecutiveLearningCount;  // 총 연속 학습 일수
    private int streaksAchieved7Days;  // 7일 단위로 몇 번 성공했는지
    private int streaksAchieved30Days;  // 30일 단위로 몇 번 성공했는지
    private List<String> learningDates;  // 학습 날짜들

    // 7일 연속 학습을 위한 생성자
    public AchievementResponse(int consecutiveLearningCount, int streaksAchieved7Days, List<String> learningDates) {
        this.consecutiveLearningCount = consecutiveLearningCount;
        this.streaksAchieved7Days = streaksAchieved7Days;
        this.learningDates = learningDates;
    }

    // 30일 연속 학습을 위한 생성자
    public AchievementResponse(int consecutiveLearningCount, int streaksAchieved30Days, List<String> learningDates, boolean is30DaysAchievement) {
        this.consecutiveLearningCount = consecutiveLearningCount;
        this.streaksAchieved30Days = streaksAchieved30Days;
        this.learningDates = learningDates;
    }

    // 7일 및 30일 연속 학습 횟수만을 반환하는 생성자
    public AchievementResponse(int streaksAchieved7Days, int streaksAchieved30Days) {
        this.streaksAchieved7Days = streaksAchieved7Days;
        this.streaksAchieved30Days = streaksAchieved30Days;
    }
}
