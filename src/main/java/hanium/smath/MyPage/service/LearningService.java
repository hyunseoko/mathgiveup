package hanium.smath.MyPage.service;

import hanium.smath.Member.repository.MemberRepository;
import hanium.smath.MyPage.dto.LearningRecordResponse;
import hanium.smath.MyPage.entity.LearningRecord;
import hanium.smath.Member.entity.Member;
import hanium.smath.MyPage.repository.LearningRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningService {

    @Autowired
    private LearningRecordRepository repository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AchievementService achievementService;

    public Mono<LearningRecordResponse> getMonthlyLearningRecords(String loginId, String yearMonth) {
        try {
            System.out.println("Starting getMonthlyLearningRecords for loginId: " + loginId + ", yearMonth: " + yearMonth);

            // YearMonth 파싱
            YearMonth ym = YearMonth.parse(yearMonth);
            System.out.println("Parsed yearMonth: " + ym);

            // 시작일과 종료일 계산
            LocalDate startDate = ym.atDay(1);
            LocalDate endDate = ym.atEndOfMonth();
            System.out.println("Calculated startDate: " + startDate + ", endDate: " + endDate);

            // 로그인 ID를 이용해 멤버 조회
            System.out.println("Attempting to find member by loginId: " + loginId);
            Member member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Member ID"));
            System.out.println("Found member: " + member);

            // 이전 달 데이터를 가져오기 위해 startDate를 조정
            LocalDate previousMonthStartDate = startDate.minusMonths(1).withDayOfMonth(1);
            System.out.println("Adjusted previousMonthStartDate: " + previousMonthStartDate);

            // 데이터베이스에서 학습 기록 조회
            System.out.println("Fetching learning records between " + previousMonthStartDate + " and " + endDate + " for member: " + member);
            List<LearningRecord> records = repository.findByMemberAndLearningDateBetween(member, previousMonthStartDate, endDate);
            System.out.println("Fetched " + records.size() + " learning records: " + records);

            // 월 단위 학습 일자 필터링
            List<String> learningDays = records.stream()
                    .filter(record -> !record.getLearningDate().isBefore(startDate) && !record.getLearningDate().isAfter(endDate))
                    .map(record -> record.getLearningDate().toString())
                    .collect(Collectors.toList());
            System.out.println("Filtered learning days for the month: " + learningDays);

            // 연속 학습일 계산
            System.out.println("Calculating consecutive learning days from records.");
            long consecutiveDays = calculateConsecutiveLearningDays(records);
            System.out.println("Calculated consecutive learning days: " + consecutiveDays);

            // 7일 연속 학습 뱃지 수여 체크
            System.out.println("Checking for achievement award for " + consecutiveDays + " consecutive learning days.");
            achievementService.awardAchievementForConsecutiveLearningDays(loginId, (int) consecutiveDays);
            System.out.println("Achievement award check completed.");

            // 연속 학습일 및 학습 날짜 리스트를 포함한 응답 생성
            LearningRecordResponse response = new LearningRecordResponse(yearMonth, learningDays, consecutiveDays);
            System.out.println("Created LearningRecordResponse: " + response);

            System.out.println("getMonthlyLearningRecords completed successfully for loginId: " + loginId);
            return Mono.just(response);
        } catch (Exception e) {
            System.err.println("Error in getMonthlyLearningRecords for loginId: " + loginId + ", yearMonth: " + yearMonth);
            System.err.println("Exception message: " + e.getMessage());
            e.printStackTrace(); // 추가적인 예외 정보 출력
            return Mono.error(e);
        }
    }

    // 연속 학습일 계산 로직
    private long calculateConsecutiveLearningDays(List<LearningRecord> records) {
        System.out.println("Starting calculation of consecutive learning days.");

        // 학습 기록을 날짜 순으로 정렬
        records.sort((record1, record2) -> record1.getLearningDate().compareTo(record2.getLearningDate()));
        System.out.println("Sorted records by learningDate: " + records);

        long maxConsecutiveDays = 0;
        long currentStreak = 0;
        LocalDate lastDate = null;

        for (LearningRecord record : records) {
            LocalDate learningDate = record.getLearningDate();
            System.out.println("Processing record with learningDate: " + learningDate);

            if (lastDate != null && !learningDate.isEqual(lastDate.plusDays(1))) {
                System.out.println("Non-consecutive day found. Resetting streak.");
                currentStreak = 0;
            }

            currentStreak++;
            lastDate = learningDate;

            System.out.println("Current streak: " + currentStreak + ", Max consecutive days: " + maxConsecutiveDays);
            maxConsecutiveDays = Math.max(maxConsecutiveDays, currentStreak);
        }

        System.out.println("Consecutive learning days calculation completed. Max consecutive days: " + maxConsecutiveDays);
        return maxConsecutiveDays;
    }
}
