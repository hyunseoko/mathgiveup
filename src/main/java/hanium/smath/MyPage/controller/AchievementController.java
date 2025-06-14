package hanium.smath.MyPage.controller;

import hanium.smath.MyPage.dto.AchievementResponse;
import hanium.smath.MyPage.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    // 7일 연속 학습 달성 관련 데이터를 반환하는 엔드포인트
    @GetMapping("/consecutive/7days")
    public ResponseEntity<AchievementResponse> getConsecutive7DayLearningAchievements() {
        // JWT로부터 인증된 회원의 로그인 ID를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName(); // 인증된 사용자의 로그인 ID를 가져옴

        AchievementResponse response = achievementService.getConsecutiveLearningData(loginId, 7);

        return ResponseEntity.ok(response);
    }

    // 30일 연속 학습 달성 관련 데이터를 반환하는 엔드포인트
    @GetMapping("/consecutive/30days")
    public ResponseEntity<AchievementResponse> getConsecutive30DayLearningAchievements() {
        // JWT로부터 인증된 회원의 로그인 ID를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName(); // 인증된 사용자의 로그인 ID를 가져옴

        AchievementResponse response = achievementService.getConsecutiveLearningData(loginId, 30);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public AchievementResponse getAllAchievements() {
        // JWT로부터 인증된 회원의 로그인 ID를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName(); // 인증된 사용자의 로그인 ID를 가져옴

        return achievementService.getAllAchievements(loginId);
    }
}
