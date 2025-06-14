package hanium.smath.Member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSkillScoreRequest {
    private int newScore;  // 새로운 실력 테스트 점수만 전달
}
