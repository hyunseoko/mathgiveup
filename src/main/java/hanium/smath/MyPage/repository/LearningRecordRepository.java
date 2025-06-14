package hanium.smath.MyPage.repository;

import hanium.smath.MyPage.entity.LearningRecord;
import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LearningRecordRepository extends JpaRepository<LearningRecord, Long> {
    List<LearningRecord> findByMemberAndLearningDateBetween(Member member, LocalDate startDate, LocalDate endDate);
}
