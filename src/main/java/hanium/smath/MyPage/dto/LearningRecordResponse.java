package hanium.smath.MyPage.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LearningRecordResponse {

    private String yearMonth;
    private List<String> learningDays;
    private long consecutiveDays;

    public LearningRecordResponse(String yearMonth, List<String> learningDays, long consecutiveDays) {
        this.yearMonth = yearMonth;
        this.learningDays = learningDays;
        this.consecutiveDays = consecutiveDays;
    }
}
