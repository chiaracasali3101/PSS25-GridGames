package it.unibo.gridgames.persistence;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Locale;

import it.unibo.gridgames.model.GameRecordImpl;
import it.unibo.gridgames.model.GameType;
import it.unibo.gridgames.persistence.PersistenceConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ScoreManagerImpl is a class that implements the ScoreManager interface and
 * provides functionality to save and load game scores from a file.
 * It uses a file path to store the scores and provides methods to save a score,
 * load all scores, get top scores, get the number of games played, get the
 * average score, and
 */

public class ScoreManagerImpl implements ScoreManager {
    private static final String GAMETYPE_NO_NULL = "GameType cannot be null";

    private static final int TIMESTAMP_INDEX = 5;

    private final Path filePath;

    /**
     * Constructor for ScoreManagerImpl that takes a file path as a parameter.
     * 
     * @param filePath the path to the file where scores will be saved and loaded
     * @throws NullPointerException if {@code filePath} is null
     */
    public ScoreManagerImpl(final String filePath) { // riceve dall'esterno il percorso del file
        this.filePath = Path.of(filePath);
    }

    // implementazione dei metodi

    /**
     * Saves a game score to the file. The method is synchronized to ensure thread
     * safety when multiple threads attempt to save scores simultaneously.
     * 
     * @param record the game record to be saved
     * @throws IOException signals that an I/O exception of some sort has occurred
     */
    @Override
    public final void saveScore(final GameRecordImpl record) throws IOException { // salvare il punteggio nel file
        synchronized (this) {

            // controllo valori nulli
            Objects.requireNonNull(record, "Record cannot be null");

            // logica per salvare il record nel file
            final String playerName = record.getPlayerName();
            final int score = record.getScore();
            final String gameType = record.getGameType().name();
            final int moves = record.getMoves();
            final long durationSeconds = record.getDurationSeconds();
            final LocalDateTime timestamp = record.getTimestamp();

            final String line = String.format("%s,%d,%s,%d,%d,%s%n", playerName, score, gameType, moves,
                    durationSeconds, timestamp);

            // scrittura nel file
            try (BufferedWriter writer = Files.newBufferedWriter(
                    this.filePath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                writer.write(line);
            }
        }
    }

    // mostra i punteggi salvati nel file, se il file non esiste ritorna una lista
    // vuota
    @Override
    public final List<GameRecordImpl> loadAllScores() {
        if (!Files.exists(this.filePath)) {
            return List.of();
        }

        try {
            final List<String> lines = Files.readAllLines(this.filePath);
            final List<GameRecordImpl> records = new ArrayList<>();

            for (final String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                final String[] tokens = line.split(",");
                if (tokens.length >= PersistenceConstants.EXPECTED_FIELDS_COUNT.getValue()) {
                    final String playerName = tokens[0].trim();
                    final int score = Integer.parseInt(tokens[1].trim());
                    final GameType gameType = GameType.valueOf(tokens[2].trim().toUpperCase(Locale.ROOT));
                    final int moves = Integer.parseInt(tokens[3].trim());
                    final long durationSeconds = Long.parseLong(tokens[4].trim());
                    final LocalDateTime timestamp = LocalDateTime.parse(tokens[TIMESTAMP_INDEX].trim());
                    final GameRecordImpl record = new GameRecordImpl(playerName, score, gameType, moves,
                            durationSeconds, timestamp);
                    records.add(record);
                }
            }
            return List.copyOf(records);
        } catch (final IOException e) {
            return List.of();
        }
    }

    @Override
    public final List<GameRecordImpl> getTopScores(final GameType gameType, final int limit) {
        Objects.requireNonNull(gameType, GAMETYPE_NO_NULL);

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        return loadAllScores().stream()
                .filter(record -> record.getGameType() == gameType)
                .sorted(Comparator.comparingInt(GameRecordImpl::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public final int getNumberOfGamesPlayed(final GameType gameType) {
        Objects.requireNonNull(gameType, GAMETYPE_NO_NULL);
        return (int) loadAllScores().stream()
                .filter(record -> record.getGameType() == gameType)
                .count();
    }

    @Override
    public final double getAverageScore(final GameType gameType) {
        Objects.requireNonNull(gameType, GAMETYPE_NO_NULL);
        return loadAllScores().stream()
                .filter(record -> record.getGameType() == gameType)
                .mapToInt(GameRecordImpl::getScore)
                .average()
                .orElse(0.0);
    }

    @Override
    public final GameRecordImpl getHighestScore(final GameType gameType) {
        Objects.requireNonNull(gameType, GAMETYPE_NO_NULL);
        return loadAllScores().stream()
                .filter(record -> record.getGameType() == gameType)
                .max(Comparator.comparingInt(GameRecordImpl::getScore))
                .orElse(null);
    }
}
