package it.unibo.gridgames.persistence;

import java.nio.file.Path;
import java.util.Objects;

import it.unibo.gridgames.model.score.GameRecordImpl;
import it.unibo.gridgames.model.score.GameType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

/**
 * ScoreManagerImpl is a class that implements the ScoreManager interface and
 * provides functionality to save and load game scores from a file.
 * It uses a file path to store the scores and provides methods to save a score,
 * load all scores, get top scores, get the number of games played, get the
 * average score, and
 */

public class ScoreManagerImpl implements ScoreManager {
    private static final String GAMETYPE_NO_NULL = "GameType cannot be null";
    private final Path filePath;

    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) 
            (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
        .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) 
            (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString()))
        .setPrettyPrinting()
        .create();

    /**
     * Default constructor for application runtime.
     */
    public ScoreManagerImpl() {
        this(Path.of("scores.json"));
    }

    /**
     * View Score.
     * 
     * @param filePath score
     */
    public ScoreManagerImpl(final Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "filePath cannot be null");
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
    public final void saveScore(final GameRecordImpl record) throws IOException {
        synchronized (this) {
            Objects.requireNonNull(record, "Record cannot be null");
            final List<GameRecordImpl> currentScores = new ArrayList<>(loadAllScores());
            currentScores.add(record);

            if (this.filePath.getParent() != null) {
                Files.createDirectories(this.filePath.getParent());
            }

            //sovrascrive json
            try (var writer = Files.newBufferedWriter(
                    this.filePath,
                    java.nio.charset.StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                this.gson.toJson(currentScores, writer);
            }
        }
    }

    // mostra i punteggi salvati nel file, se il file non esiste ritorna una lista
    // vuota
    @Override
    public final List<GameRecordImpl> loadAllScores() {
        if (!Files.exists(this.filePath) || !Files.isRegularFile(this.filePath)) {
            return List.of();
        }

        try (var reader = Files.newBufferedReader(this.filePath, java.nio.charset.StandardCharsets.UTF_8)) {
            final java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<GameRecordImpl>>() { }.getType();
            final List<GameRecordImpl> records = this.gson.fromJson(reader, listType);
            return records != null ? List.copyOf(records) : List.of();
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
