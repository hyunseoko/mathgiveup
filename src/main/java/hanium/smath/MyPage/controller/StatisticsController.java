package hanium.smath.MyPage.controller;

import hanium.smath.MyPage.dto.DailyStatisticsResponse;
import hanium.smath.MyPage.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/date/{date}")
    public CompletableFuture<ResponseEntity<DailyStatisticsResponse>> getDailyStatistics(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // 현재 인증된 사용자의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName(); // 인증된 사용자의 로그인 ID를 가져옴

        System.out.println("Received request for daily statistics. User ID: " + loginId + ", Date: " + date);

        return statisticsService.getDailyStatistics(loginId, date)
                .thenApply(response -> {
                    System.out.println("Returning response for user: " + loginId + " on date: " + date);
                    return ResponseEntity.ok(response);
                });
    }
}
