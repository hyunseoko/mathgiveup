package hanium.smath.MyPage.entity;

import hanium.smath.Member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "game_sessions")
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSession; // MySQL에서 자동으로 생성하는 id

    @ManyToOne
    @JoinColumn(name = "login_id", referencedColumnName = "login_id", nullable = false)
    private Member member;

    @Column(name = "session_date", nullable = false)
    private String sessionDate; // 학습날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private GameType gameType; // 게임 타입 (stage, friend_match, custom)

    @Column(name = "problems_solved", nullable = false)
    private int problemsSolved; // 풀었던 문제 수

    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers; // 맞춘 문제 수
}
