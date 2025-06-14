package hanium.smath.Community.entity;

import hanium.smath.Member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPost;

    @ManyToOne
    @JoinColumn(name = "login_id", referencedColumnName = "login_id", nullable = false)
    private Member member;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "content", length = 1000, nullable = false)
    private String content;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;


    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdTime = now;
        updatedTime = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}
