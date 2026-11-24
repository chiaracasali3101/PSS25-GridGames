package it.unibo.gridgames.model.score;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to compute statistics on game records.
 */
public final class ScoreStats {
    private ScoreStats() { //costruttore 
    }

    /**
     * Calculates the average score from a list of GameRecordImpl objects.
     * 
     * @param scores the list of GameRecordImpl objects to calculate the average score from
     * @return a map where the key is the GameType and the value is the average score for that game type
     */
    public static Map<GameType, Double> calculateAverage(final List<GameRecordImpl> scores) {
         if(scores == null || scores.isEmpty()) {
            return Collections.emptyMap();
        }
        return scores.stream()
            .collect(Collectors.groupingBy(
                GameRecordImpl::getGameType,
                Collectors.averagingInt(GameRecordImpl::getScore)
            ));
    }
}
