package it.unibo.gridgames.persistence;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;

import java.io.IOException;
import java.util.List;

/**
 * Interface defining the persistence operations and statistical queries for managing game score records.
 */

// interfaccia per la gestione dei punteggi dei giochi
public interface ScoreManager {

    /**
     * Saves a new game score record.
     * 
     * @param record the game record to save
     * @throws IOException          if an error occurs while writing to storage
     * @throws NullPointerException if record is null
     */

    // salva il punteggio di una partita appena finita nel file di persistenza
    void saveScore(GameRecordImpl record) throws IOException;

    /**
     * Loads all game score records from storage.
     * 
     * @return a list of all game records
     * @throws IOException if an error occurs while reading from storage
     */
    // riapre il file e converte tutti i record in oggetti GameRecordImpl
    // restituendo la lista
    List<GameRecordImpl> loadAllScores() throws IOException;

    /**
     * Retrieves the top scores for a specific game type, limited to a specified number of records.
     * 
     * @param gameType the type of game for which to retrieve top scores
     * @param limit the maximum number of top scores to retrieve
     * @return a list of top game records for the specified game type
     */
    // estrae la classifica filtrando il gioco e limitando il numero di record
    // visualizzati
    List<GameRecordImpl> getTopScores(GameType gameType, int limit);

    /**
     * Retrieves the number of games played for a specific game type.
     * 
     * @param gameType the type of game for which to count the number of games played
     * @return the number of games played for the specified game type
     */
    // estrae il numero delle partite giocate di un gioco
    int getNumberOfGamesPlayed(GameType gameType);

    /**
     * Retrieves the average score for a specific game type.
     * 
     * @param gameType the type of game for which to calculate the average score
     * @return the average score for the specified game type
     */
    // restituisce la media dei punteggi di un gioco specifico
    double getAverageScore(GameType gameType);

    /**
     * Retrieves the highest score for a specific game type.
     * 
     * @param gameType the type of game for which to retrieve the highest score
     * @return the game record with the highest score for the specified game type
     */
    // mostra il punteggio più alto di un gioco specifico
    GameRecordImpl getHighestScore(GameType gameType);
}
