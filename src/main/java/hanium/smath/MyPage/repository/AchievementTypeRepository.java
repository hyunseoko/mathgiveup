package hanium.smath.MyPage.repository;

import hanium.smath.MyPage.entity.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementTypeRepository extends JpaRepository<AchievementType, Integer> {
}
