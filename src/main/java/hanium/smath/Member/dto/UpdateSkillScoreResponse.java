package hanium.smath.Member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSkillScoreResponse {
    private String rankLevel;  // 유저의 새로운 Rank
    private int skillScore;    // 유저의 스킬 테스트 점수
    private String nickname;   // 유저의 닉네임
}
