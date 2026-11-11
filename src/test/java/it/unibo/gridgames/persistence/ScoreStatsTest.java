package it.unibo.gridgames.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;
import it.unibo.gridgames.model.score.ScoreStats;

class ScoreStatsTest {

    @Test
    void testCalculateAverageWithEmptyList() {
        final Map<GameType, Double> avgEmpty = ScoreStats.calculateAverage(Collections.emptyList());
        assertTrue(avgEmpty.isEmpty());

        final Map<GameType, Double> avgNull = ScoreStats.calculateAverage(null);
        assertTrue(avgNull.isEmpty());
    }

    @Test
    void testCalculateAverage() {
        final List<GameRecordImpl> records = List.of(
            new GameRecordImpl("Player1", 1000, GameType.SUDOKU, 10, 120L, LocalDateTime.now()),
            new GameRecordImpl("Player2", 2000, GameType.SUDOKU, 20, 240L, LocalDateTime.now())
        );

        final Map<GameType, Double> averages = ScoreStats.calculateAverage(records);

        assertEquals(1, averages.size());
        assertEquals(1500.0, averages.get(GameType.SUDOKU), 0.001);
    }
}