package hanium.smath.MyPage.service;

import hanium.smath.MyPage.dto.AchievementResponse;
import hanium.smath.MyPage.entity.Achievement;
import hanium.smath.MyPage.entity.AchievementType;
import hanium.smath.Member.entity.Member;
import hanium.smath.MyPage.repository.AchievementRepository;
import hanium.smath.MyPage.repository.AchievementTypeRepository;
import hanium.smath.Member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private AchievementTypeRepository achievementTypeRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 7일 및 30일 연속 학습 달성 뱃지 수여 메서드
    public void awardAchievementForConsecutiveLearningDays(String loginId, int consecutiveDays) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Member ID"));

        if (consecutiveDays >= 7) {
            awardAchievement(member, "7일 연속 학습 달성", 1); // Assuming 1 is the ID for 7-day streak badge
        }

        if (consecutiveDays >= 30) {
            awardAchievement(member, "30일 연속 학습 달성", 2); // Assuming 2 is the ID for 30-day streak badge
        }
    }

    // 특정 학습 뱃지 수여 메서드
    private void awardAchievement(Member member, String achievementValue, int achievementTypeId) {
        AchievementType type = achievementTypeRepository.findById(achievementTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Achievement Type ID"));

        Achievement achievement = Achievement.builder()
                .member(member)
                .achievementValue(achievementValue)
                .createTime(LocalDateTime.now())
                .achievementType(type)
                .build();

        achievementRepository.save(achievement);
        System.out.println("Achievement awarded to member: " + member.getLoginId());
    }

    // 연속 학습 데이터를 가져오는 메서드
    public AchievementResponse getConsecutiveLearningData(String loginId, int days) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Member ID"));

        // 연속 학습 달성 횟수 계산
        int consecutiveLearningCount = calculateConsecutiveLearningCount(member, days);

        // 학습 날짜 리스트 가져오기
        List<String> learningDates = getLearningDates(member);

        if (days == 7) {
            int streaksAchieved7Days = consecutiveLearningCount / 7;
            return new AchievementResponse(consecutiveLearningCount, streaksAchieved7Days, learningDates);
        } else if (days == 30) {
            int streaksAchieved30Days = consecutiveLearningCount / 30;
            return new AchievementResponse(consecutiveLearningCount, streaksAchieved30Days, learningDates, true);
        }

        // 기본적으로 7일 및 30일 연속 학습 데이터를 모두 포함한 경우 반환
        int streaksAchieved7Days = calculateConsecutiveLearningCount(member, 7) / 7;
        int streaksAchieved30Days = calculateConsecutiveLearningCount(member, 30) / 30;
        return new AchievementResponse(consecutiveLearningCount, streaksAchieved7Days, streaksAchieved30Days, learningDates);
    }

    public AchievementResponse getAllAchievements(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Member ID"));

        // 7일 연속 학습 달성 횟수 계산
        int streaksAchieved7Days = calculateConsecutiveLearningCount(member, 7) / 7;

        // 30일 연속 학습 달성 횟수 계산
        int streaksAchieved30Days = calculateConsecutiveLearningCount(member, 30) / 30;

        // 7일 및 30일 연속 학습 횟수만 반환
        return new AchievementResponse(streaksAchieved7Days, streaksAchieved30Days);
    }

    // 연속 학습 달성 횟수 계산 메서드
    private int calculateConsecutiveLearningCount(Member member, int days) {
        String achievementValue = days == 7 ? "7일 연속 학습 달성" : "30일 연속 학습 달성";
        List<Achievement> achievements = achievementRepository.findByMember(member);
        return (int) achievements.stream()
                .filter(a -> achievementValue.equals(a.getAchievementValue()))
                .count();
    }

    // 학습 날짜 리스트 반환 메서드
    private List<String> getLearningDates(Member member) {
        return achievementRepository.findByMember(member).stream()
                .filter(a -> a.getAchievementValue().contains("연속 학습 달성")) // 7일 또는 30일 연속 학습 달성에 해당하는 기록만 필터링
                .map(a -> a.getCreateTime().toString())
                .collect(Collectors.toList());
    }
}
