package hanium.smath.Member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "emailverification")
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmailVerification;

    @OneToOne
    @JoinColumn(name = "login_id", referencedColumnName = "login_id")
    private Member member;

    @Column (name="email", length = 100, nullable = false, unique = true)
    private String email;

    private int verificationCode;
    private boolean verifiedEmail;
    private LocalDateTime createTime;

    // 기본 생성자
    public EmailVerification() {
        this.verifiedEmail = false; // 새 인증 항목에 대한 기본값
        this.createTime = LocalDateTime.now(); // 생성 시간을 현재 시간으로 설정
    }

    // 편의를 위한 파라미터 생성자
    public EmailVerification(Member member, int verificationCode) {
        this.verifiedEmail = false; // 새 인증 항목에 대한 기본값
        this.createTime = LocalDateTime.now(); // 생성 시간을 현재 시간으로 설정
    }

    // 인증 코드를 업데이트하는 메서드
    public void updateVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
        this.verifiedEmail = false; // 코드가 변경되면 다시 검증 필요
        this.createTime = LocalDateTime.now(); // 코드 갱신 시간 업데이트
    }

}
