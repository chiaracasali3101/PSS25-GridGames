package it.unibo.gridgames.model.score;

//interfaccia 

import java.time.LocalDateTime;

/**
 * Represents a record of a game played by a player.
 * GameRecord.
 */
public interface GameRecord {
    /**
     * Returns the name of the player associated with this game record.
     * 
     * @return player's name
     */
    String getPlayerName();

    /**
     * Returns the score achieved by the player in this game record.
     * 
     * @return score achieved by the player
     */
    int getScore();

    /**
     * Returns the type of game associated with this game record.
     * 
     * @return game type (e.g., 2048, Sudoku, 15Game)
     */
    GameType getGameType();

    /**
     * Returns the number of moves made by the player in this game record.
     * 
     * @return number of moves made by the player
     */
    int getMoves();

    /**
     * Returns the duration of the game in seconds for this game record.
     * 
     * @return duration of the game in seconds
     */
    long getDurationSeconds();

    /**
     * Returns the timestamp indicating when this game record was created or recorded.
     * 
     * @return timestamp of the game record
     */
    LocalDateTime getDateTime();
}
