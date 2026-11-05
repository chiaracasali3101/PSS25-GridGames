package it.unibo.gridgames.model.game2048;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link Game2048} on a square board, 4x4 for a new match.
 * The match is won as soon as a 2048 tile appears and is lost when no move can
 * change the grid; in both cases no further move is accepted.
 */
public final class Game2048Impl implements Game2048 {

    private static final int SIZE = 4;
    private static final int INITIAL_TILES = 2;
    private static final int WINNING_TILE = 2048;

    private final TileSpawner spawner;
    private final BoardMover mover = new BoardMoverImpl();
    private Board board;
    private int score;
    private int moves;
    private Optional<Snapshot> undoPoint = Optional.empty();

    /**
     * Creates a new match with two random tiles.
     */
    public Game2048Impl() {
        this(new TileSpawnerImpl());
    }

    /**
     * Creates a new match whose two initial tiles are placed by the given spawner.
     *
     * @param spawner the strategy used to add new tiles
     */
    public Game2048Impl(final TileSpawner spawner) {
        this(new int[SIZE][SIZE], spawner);
        for (int i = 0; i < INITIAL_TILES; i++) {
            this.spawner.spawn(this.board);
        }
    }

    /**
     * Creates a match starting from the given grid, without adding initial tiles.
     * The grid is copied.
     *
     * @param grid the initial cell values indexed by [row][column]
     * @param spawner the strategy used to add new tiles
     * @throws IllegalArgumentException if the grid is not square or contains invalid values
     */
    public Game2048Impl(final int[][] grid, final TileSpawner spawner) {
        this.board = new BoardImpl(grid);
        this.spawner = Objects.requireNonNull(spawner);
    }

    @Override
    public boolean move(final Direction direction) {
        Objects.requireNonNull(direction);
        if (getState() != GameState2048.PLAYING) {
            return false;
        }
        final Snapshot beforeMove = new Snapshot(new BoardImpl(this.board.toMatrix()), this.score, this.moves);
        final MoveResult result = this.mover.move(this.board, direction);
        if (result.changed()) {
            this.undoPoint = Optional.of(beforeMove);
            this.score += result.points();
            this.moves++;
            this.spawner.spawn(this.board);
        }
        return result.changed();
    }

    @Override
    public boolean undo() {
        if (getState() != GameState2048.PLAYING || this.undoPoint.isEmpty()) {
            return false;
        }
        final Snapshot snapshot = this.undoPoint.get();
        this.board = snapshot.board();
        this.score = snapshot.score();
        this.moves = snapshot.moves();
        this.undoPoint = Optional.empty();
        return true;
    }

    @Override
    public int[][] getGrid() {
        return this.board.toMatrix();
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public int getMoves() {
        return this.moves;
    }

    @Override
    public GameState2048 getState() {
        final int[][] grid = this.board.toMatrix();
        if (Arrays.stream(grid).flatMapToInt(Arrays::stream).anyMatch(value -> value >= WINNING_TILE)) {
            return GameState2048.WON;
        }
        if (this.board.getEmptyCells().isEmpty() && !hasEqualNeighbours(grid)) {
            return GameState2048.GAME_OVER;
        }
        return GameState2048.PLAYING;
    }

    // basta confrontare ogni cella con quella a destra e quella sotto
    private static boolean hasEqualNeighbours(final int[][] grid) {
        final int size = grid.length;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                final int value = grid[row][column];
                if (column + 1 < size && value == grid[row][column + 1]
                    || row + 1 < size && value == grid[row + 1][column]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Memento of the match taken before a move.
     *
     * @param board a private copy of the board
     * @param score the score before the move
     * @param moves the move counter before the move
     */
    private record Snapshot(Board board, int score, int moves) { }
}
