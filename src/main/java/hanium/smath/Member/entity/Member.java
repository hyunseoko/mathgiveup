package hanium.smath.Member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Member")
    private int idMember;

    @Column(name="email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "login_id", length = 30, nullable = true, unique = true)
    private String loginId;

    @Column(name = "login_pwd", length = 300, nullable = true)
    private String loginPwd;

    @Column(name = "name", length = 30, nullable = true)
    private String name;

    @Column(name = "nickname", length = 30, nullable = true, unique = true)
    private String nickname;

    @Column(name = "grade", length = 10, nullable = true)
    private Integer grade;

    @Column(name = "id_level", nullable = true)
    private int idLevel;

    @Column(name = "school", nullable = true)
    private String school;

    @Column(name = "birthdate", nullable = true)
    private LocalDate birthdate;

    @Column(name = "is_email_verified", nullable = true)
    private boolean isEmailVerified;

    @CreationTimestamp
    @Column(name = "create_time", nullable = true, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime;

    @Column(name="icon", nullable = true)
    private String icon;

    @Column(name = "google_id", nullable = true, unique = true)
    private String googleId; // 구글 아이디 추가

    @Column(name = "kakao_id", nullable = true, unique = true)
    private String kakaoId;

    @Builder.Default
    @Column(name = "skill_score", nullable = true)  // nullable을 false로 설정
    private Integer skillScore = 0;  // 기본값 0 설정 (수정됨)

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_level", nullable = true)
    private Rank rank;

    // 마지막 로그인 시간 추가
    @Column(name = "last_login_time", nullable = true)
    private Timestamp lastLoginTime;  // 마지막 로그인 시간
}
