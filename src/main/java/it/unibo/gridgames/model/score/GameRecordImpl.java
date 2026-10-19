package it.unibo.gridgames.model.score;

import java.time.LocalDateTime;
import java.util.Objects;

//classe 

/**
 * Represents an immutable record of a completed game session.
 */

public class GameRecordImpl implements GameRecord {
    private final String playerName;
    private final int score;
    private final GameType gameType;
    private final int moves;
    private final long durationSeconds;
    private final LocalDateTime timestamp;

    /**
     * Constructs a new {@code GameRecordImpl} instance.
     * 
     * @param playerName      the name of the player
     * @param score2           the score achieved by the player
     * @param gameType        the type of the game played
     * @param moves           the number of moves made by the player
     * @param durationSeconds the duration of the game in seconds
     * @param timestamp       the timestamp of when the game was played
     * @throws IllegalArgumentException if playerName is null or blank, or if score, moves, or durationSeconds are negative
     * @throws NullPointerException     if gameType or timestamp is null
     */

    // costruttore per il record di gioco
    public GameRecordImpl(final String playerName, final int score2, final GameType gameType, final int moves,
            final long durationSeconds, final LocalDateTime timestamp) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be null or blank");
        }
        if (playerName.contains(",")) {
            throw new IllegalArgumentException("Player name cannot contain commas");
        }
        if (score2 < 0 || moves < 0 || durationSeconds < 0) {
            throw new IllegalArgumentException("Metrics cannot be negative");
        }

        this.playerName = playerName;
        this.score = score2;
        this.gameType = Objects.requireNonNull(gameType, "GameType cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.moves = moves;
        this.durationSeconds = durationSeconds;
    }

    // metodi getter pubblici per accedere ai campi privati della classe
    // nome giocatore
    /**
     * Returns the name of the player who played the game.
     * 
     * @return the name of the player who played the game
     */
    public final String getPlayerName() {
        return this.playerName;
    }

    // gioco
    /**
     * Returns the type of game that was played.
     * 
     * @return the type of game that was played
     */
    public final GameType getGameType() {
        return this.gameType;
    }

    // punteggio
    /**
     * Returns the score achieved by the player in the game.
     * 
     * @return the score achieved by the player in the game
     */
    public final int getScore() {
        return this.score;
    }

    // mosse
    /**
     * Returns the number of moves made by the player in the game.
     * 
     * @return the number of moves made by the player in the game
     */
    public final int getMoves() {
        return this.moves;
    }

    // tempo
    /**
     * Returns the duration of the game in seconds.
     * 
     * @return the duration of the game in seconds
     */
    public final long getDurationSeconds() {
        return this.durationSeconds;
    }

    // quando è stata giocata la partita
    /**
     * Returns the timestamp of when the game was played.
     * 
     * @return the timestamp of when the game was played
     */
    public final LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    /**
     * Compares this game record to another object for equality.
     *
     * @param obj the object to compare with
     * @return true if both objects have identical fields, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final GameRecordImpl other = (GameRecordImpl) obj;
        return this.score == other.score
            && this.moves == other.moves
            && this.durationSeconds == other.durationSeconds
            && Objects.equals(this.playerName, other.playerName)
            && this.gameType == other.gameType
            && Objects.equals(this.timestamp, other.timestamp);
    }

    /**
     * Generates the hash code for this game record.
     *
     * @return the integer hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(
            this.playerName,
            this.score,
            this.gameType,
            this.moves,
            this.durationSeconds,
            this.timestamp
        );
    }

    /**
     * Returns a string representation of this game record.
     *
     * @return a formatted string containing the record details
     */
    @Override
    public String toString() {
        return "GameRecordImpl{"
            + "playerName='" + this.playerName + '\''
            + ", score=" + this.score
            + ", gameType=" + this.gameType
            + ", moves=" + this.moves
            + ", durationSeconds=" + this.durationSeconds
            + ", timestamp=" + this.timestamp
            + '}';
    }
}
