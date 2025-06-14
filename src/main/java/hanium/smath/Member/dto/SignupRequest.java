package hanium.smath.Member.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignupRequest {
    private String email;
    private String name;
    private String loginId;
    private String loginPwd;
    private String nickname;
    private int grade;
    private String birthdate; // 생년월일 추가
    private String school; // 학교 추가
}
