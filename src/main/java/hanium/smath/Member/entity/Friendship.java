package hanium.smath.Member.entity;

import hanium.smath.Member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "login_id", nullable = false)
    private Member member;  // 현재 사용자를 가리킴

    @ManyToOne
    @JoinColumn(name = "friend_id", referencedColumnName = "login_id", nullable = false)
    private Member friend;  // 친구를 가리킴

    @Column(name = "status", nullable = false)
    private String status;  // "PENDING", "ACCEPTED", "REJECTED" 등 상태 관리
}
