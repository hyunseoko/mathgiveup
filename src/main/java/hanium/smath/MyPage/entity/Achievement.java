package hanium.smath.MyPage.entity;

import hanium.smath.Member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "achievement")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAchievement;

    @ManyToOne
    @JoinColumn(name = "login_id", referencedColumnName = "login_id", nullable = false)
    private Member member;

    @Lob
    private String achievementValue;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @ManyToOne
    @JoinColumn(name = "idAchievementType", nullable = false)
    private AchievementType achievementType;
}
