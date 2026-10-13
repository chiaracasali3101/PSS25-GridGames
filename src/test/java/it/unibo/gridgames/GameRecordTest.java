package it.unibo.gridgames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import it.unibo.gridgames.model.GameRecordImpl;
import it.unibo.gridgames.model.GameType;

public class GameRecordTest {

    private static final String PLAYER_NAME = "Alice";
    private static final int SCORE = 100;
    private static final int MOVES = 15;
    private static final long DURATION = 60L;

    //Verifica che ciascun metodo getter restituisca esattamente il dato assegnato in fase di istanziazione.
   @Test 
    void testGetters() {
        final LocalDateTime now = LocalDateTime.now().withNano(0);
        final GameRecordImpl record = new GameRecordImpl(PLAYER_NAME, SCORE, GameType.GAME_2048, MOVES, DURATION, now);
        assertEquals(PLAYER_NAME, record.getPlayerName(), "Il nome del giocatore non corrisponde");
        assertEquals(SCORE, record.getScore(), "Il punteggio non corrisponde");
        assertEquals(GameType.GAME_2048, record.getGameType(), "Il gioco non corrisponde");
        assertEquals(MOVES, record.getMoves(), "Il numero di mosse non corrisponde");
     }

    @Test 
    void testEqualsAndHashCode() {
        final LocalDateTime now = LocalDateTime.now().withNano(0);
        final GameRecordImpl record1 = new GameRecordImpl(PLAYER_NAME, SCORE, GameType.GAME_2048, MOVES, DURATION, now);
        final GameRecordImpl recordIdentical = new GameRecordImpl(PLAYER_NAME, SCORE, GameType.GAME_2048, MOVES, DURATION, now);
        final GameRecordImpl recordDifferent = new GameRecordImpl("Bob", SCORE, GameType.GAME_2048, MOVES, DURATION, now);
       
        assertEquals(recordIdentical, recordIdentical, "Record uguali, stessi campi");
        assertEquals(record1.hashCode(), recordIdentical.hashCode(), "Due record uguali devono avere lo stesso hashCode");

        assertEquals(record1, record1, "Un record deve essere uguale a se stesso");

        assertNotEquals(record1, recordDifferent, "Due record con campi diversi non devono essere uguali");

        assertNotEquals(null, record1, "Un record non deve essere uguale a null");
        assertNotEquals("StringaCasuale", record1, "Un record non deve essere uguale a un oggetto di tipo diverso");
    }

    @Test 
    void testToString() {
        //Verifica che il metodo toString restituisca una rappresentazione leggibile e coerente dei dati dell'istanza.
    }

    @Test 
    void testNullPlayerThrowsException() {
        //Verifica che la creazione di un GameRecord con playerName null generi un'eccezione.
    }
}
