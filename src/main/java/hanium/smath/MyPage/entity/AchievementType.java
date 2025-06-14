package hanium.smath.MyPage.entity;

import hanium.smath.Member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "achievementtype")
public class AchievementType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAchievementType;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "achievementType")
    private List<Achievement> achievements;
}
