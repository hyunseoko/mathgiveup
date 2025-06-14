package hanium.smath.MyPage.dto;

import lombok.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStatisticsResponse {
    private Map<String, GameStatistics> gameStatistics = new HashMap<>();
    private int totalProblemsSolved;
    private double accuracyRate;

    public void addGameStatistics(String gameType, int problemsSolved, int correctAnswers) {
        gameStatistics.putIfAbsent(gameType, new GameStatistics(gameType));
        GameStatistics stats = gameStatistics.get(gameType);
        stats.addProblemsSolved(problemsSolved);
        stats.addCorrectAnswers(correctAnswers);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameStatistics {
        private String gameType;
        private int problemsSolved;
        private int correctAnswers;

        public GameStatistics(String gameType) {
            this.gameType = gameType;
        }

        public void addProblemsSolved(int problemsSolved) {
            this.problemsSolved += problemsSolved;
        }

        public void addCorrectAnswers(int correctAnswers) {
            this.correctAnswers += correctAnswers;
        }

        public double getAccuracyRate() {
            return problemsSolved > 0 ? (double) correctAnswers / problemsSolved * 100 : 0;
        }
    }
}
