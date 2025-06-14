package hanium.smath.MyPage.repository;

import hanium.smath.MyPage.entity.GameSession;
import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByMember(Member member);
}
