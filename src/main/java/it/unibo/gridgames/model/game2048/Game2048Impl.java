package it.unibo.gridgames.model.game2048;

import java.util.Objects;

/**
 * Implementation of {@link Game2048} on a 4x4 board.
 */
public final class Game2048Impl implements Game2048 {

    private static final int SIZE = 4;
    private static final int INITIAL_TILES = 2;

    private final Board board;
    private final TileSpawner spawner;
    private final BoardMover mover = new BoardMoverImpl();
    private int score;
    private int moves;

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
        final MoveResult result = this.mover.move(this.board, direction);
        if (result.changed()) {
            this.score += result.points();
            this.moves++;
            this.spawner.spawn(this.board);
        }
        return result.changed();
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
        return GameState2048.PLAYING;
    }
}
