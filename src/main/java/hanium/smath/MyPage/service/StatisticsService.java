package hanium.smath.MyPage.service;

import hanium.smath.MyPage.dto.DailyStatisticsResponse;
import hanium.smath.MyPage.entity.GameSession;
import hanium.smath.Member.entity.Member;
import hanium.smath.MyPage.entity.GameType;
import hanium.smath.MyPage.repository.StatisticsRepository;
import hanium.smath.Member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private MemberRepository memberRepository;

    public CompletableFuture<DailyStatisticsResponse> getDailyStatistics(String login_id, LocalDate date) {
        System.out.println("Fetching daily statistics for user: " + login_id + " on date: " + date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = date.format(formatter);

        return CompletableFuture.supplyAsync(() -> {
            // Member를 loginId로 조회
            Member member = memberRepository.findByLoginId(login_id)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            List<GameSession> sessions = statisticsRepository.findByMember(member)
                    .stream()
                    .filter(session -> session.getSessionDate().equals(formattedDate))
                    .collect(Collectors.toList());

            DailyStatisticsResponse response = new DailyStatisticsResponse();
            int totalProblems = 0;
            int totalCorrect = 0;

            for (GameSession session : sessions) {
                response.addGameStatistics(session.getGameType().name(), session.getProblemsSolved(), session.getCorrectAnswers());

                totalProblems += session.getProblemsSolved();
                totalCorrect += session.getCorrectAnswers();
            }

            response.setTotalProblemsSolved(totalProblems);
            response.setAccuracyRate(totalProblems > 0 ? (double) totalCorrect / totalProblems * 100 : 0);  // 백분율
            System.out.println("Total problems solved: " + totalProblems + ", Accuracy rate: " + response.getAccuracyRate());

            return response;
        }).exceptionally(e -> {
            System.err.println("Error fetching game sessions: " + e.getMessage());
            return null; // 예외 발생 시 null 반환
        });
    }
}
