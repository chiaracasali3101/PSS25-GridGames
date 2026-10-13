package it.unibo.gridgames;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import it.unibo.gridgames.model.GameRecordImpl;
import it.unibo.gridgames.model.GameType;

/**
 * Unit tests for the GameRecord class.
 * GameRecordTest 
 */
class GameRecordTest {

    private static final String PLAYER_NAME = "Alice";
    private static final int SCORE = 100;
    private static final int MOVES = 15;
    private static final long DURATION = 60L;

    /**
     * Tests that all getter methods return the correct values set during initialization.
     */
    @Test
    void testGetters() {
        final LocalDateTime now = LocalDateTime.now().withNano(0);
        final GameRecordImpl record = new GameRecordImpl(PLAYER_NAME, SCORE, GameType.GAME_2048, MOVES, DURATION, now);
        assertEquals(PLAYER_NAME, record.getPlayerName(), "Il nome del giocatore non corrisponde");
        assertEquals(SCORE, record.getScore(), "Il punteggio non corrisponde");
        assertEquals(GameType.GAME_2048, record.getGameType(), "Il gioco non corrisponde");
        assertEquals(MOVES, record.getMoves(), "Il numero di mosse non corrisponde");
     }
}
