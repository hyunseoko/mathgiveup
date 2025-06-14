package hanium.smath.Member.repository;

import hanium.smath.Member.entity.Friendship;
import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByMember(Member member);
    List<Friendship> findByFriend(Member friend);
    Optional<Friendship> findByMemberAndFriend(Member member, Member friend);
}
