package hanium.smath.Community.repository;

import hanium.smath.Community.entity.Comment;
import hanium.smath.Community.entity.Post;
import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMember(Member member);
    List<Comment> findByPost(Post post);
}
