package hanium.smath.MyPage.repository;

import hanium.smath.MyPage.entity.Achievement;
import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    List<Achievement> findByMember(Member member);
}
