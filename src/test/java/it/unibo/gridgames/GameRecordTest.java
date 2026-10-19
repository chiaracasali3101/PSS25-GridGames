package it.unibo.gridgames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;

/**
 * Unit tests for the GameRecord class.
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
        final GameRecordImpl record = new GameRecordImpl(
            PLAYER_NAME, SCORE, GameType.GAME_2048, MOVES, DURATION, now
        );

        assertEquals(PLAYER_NAME, record.getPlayerName(), "Il nome del giocatore non corrisponde");
        assertEquals(SCORE, record.getScore(), "Il punteggio non corrisponde");
        assertEquals(GameType.GAME_2048, record.getGameType(), "Il gioco non corrisponde");
        assertEquals(MOVES, record.getMoves(), "Il numero di mosse non corrisponde");
        assertEquals(DURATION, record.getDurationSeconds(), "La durata non corrisponde");
        assertEquals(now, record.getTimestamp(), "La data non corrisponde");
    }

    /**
     * Tests the equality and hashCode contract for GameRecord instances.
     */
    @Test
    void testEqualsAndHashCode() {
        final LocalDateTime now = LocalDateTime.now().withNano(0);

        final GameRecordImpl record1 = new GameRecordImpl(
            PLAYER_NAME, SCORE, GameType.GAME_2048, MOVES, DURATION, now
        );
        final GameRecordImpl recordIdentical = new GameRecordImpl(
            PLAYER_NAME, SCORE, GameType.GAME_2048, MOVES, DURATION, now
        );
        final GameRecordImpl recordDifferent = new GameRecordImpl(
            "Bob", SCORE, GameType.GAME_2048, MOVES, DURATION, now
        );

        assertEquals(record1, recordIdentical, "Record con stessi campi devono essere uguali");
        assertEquals(record1.hashCode(), recordIdentical.hashCode(), "Due record uguali devono avere lo stesso hashCode");
        assertEquals(record1, record1, "Un record deve essere uguale a se stesso");
        assertNotEquals(record1, recordDifferent, "Record con campi diversi non devono essere uguali");
        assertNotEquals(null, record1, "Un record non deve essere uguale a null");
        assertNotEquals("AltroTipo", record1, "Un record non deve essere uguale a un oggetto di tipo diverso");
    }

    /**
     * Tests that toString returns a non-null string containing the key record information.
     */
    @Test
    void testToString() {
        final LocalDateTime now = LocalDateTime.now().withNano(0);
        final GameRecordImpl record = new GameRecordImpl(
            PLAYER_NAME, SCORE, GameType.GAME_2048, MOVES, DURATION, now
        );

        final String stringRepresentation = record.toString();

        assertNotNull(stringRepresentation, "La rappresentazione testuale non deve essere nulla");
        assertTrue(stringRepresentation.contains(PLAYER_NAME), "La stringa deve contenere il nome del giocatore");
        assertTrue(stringRepresentation.contains(String.valueOf(SCORE)), "La stringa deve contenere il punteggio");
    }

    /**
     * Tests that instantiating a record with a null player name throws an exception.
     */
    @Test
    void testNullPlayerThrowsException() {
        final LocalDateTime now = LocalDateTime.now().withNano(0);

        assertThrows(
            IllegalArgumentException.class,
            () -> new GameRecordImpl(null, SCORE, GameType.GAME_2048, MOVES, DURATION, now),
            "Deve lanciare IllegalArgumentException se il nome è nullo"
        );
    }
}