package it.unibo.gridgames.model.score;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to compute statistics on game records.
 */
public class ScoreStats {
    private ScoreStats() { //costruttore 
    }

    /**
     * Calculates the average score from a list of GameRecordImpl objects.
     * @param scores
     * @return
     */
    public static Map<GameType, Double> calculateAverage(final List<GameRecordImpl> scores){
        if(scores == null || scores.isEmpty()){
            return Collections.emptyMap();
        }
        return scores.stream()
            .collect(Collectors.groupingBy(
                GameRecordImpl::getGameType,
                Collectors.averagingInt(GameRecordImpl::getScore)
            ));
    }
}
