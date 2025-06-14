package hanium.smath.MyPage.controller;

import hanium.smath.MyPage.dto.LearningRecordResponse;
import hanium.smath.MyPage.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/learning")
public class LearningController {

    @Autowired
    private LearningService service;

    @GetMapping("/records/monthly")
    public Mono<LearningRecordResponse> getMonthlyLearningRecords(@RequestParam String yearMonth) {
        // 현재 인증된 사용자의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName(); // JWT에서 추출한 로그인 ID

        // 서비스 호출 시 로그인 ID와 월 정보를 전달
        return service.getMonthlyLearningRecords(loginId, yearMonth);
    }
}
