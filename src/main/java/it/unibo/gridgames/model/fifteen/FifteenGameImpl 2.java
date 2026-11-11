package it.unibo.gridgames.model.fifteen;

/**
 * Default implementation of {@link FifteenGame}.
 */
public final class FifteenGameImpl implements FifteenGame {

    private final FifteenShuffler shuffler;
    private FifteenBoard board;

    /**
     * Creates a game and starts it on a shuffled board.
     *
     * @param shuffler the source of the initial boards
     * @throws IllegalArgumentException if the shuffler is null
     */
    public FifteenGameImpl(final FifteenShuffler shuffler) {
        if (shuffler == null) {
            throw new IllegalArgumentException("The shuffler must not be null");
        }
        this.shuffler = shuffler;
        this.board = shuffler.shuffle();
    }

    @Override
    public FifteenGameState getState() {
        return this.board.isSolved() ? FifteenGameState.WON : FifteenGameState.PLAYING;
    }

    @Override
    public int[][] getTiles() {
        return this.board.getTiles();
    }

    @Override
    public int getMoveCount() {
        return this.board.getMoveCount();
    }

    @Override
    public boolean move(final Direction direction) {
        return getState() == FifteenGameState.PLAYING && this.board.move(direction);
    }

    @Override
    public boolean moveTile(final int row, final int column) {
        return getState() == FifteenGameState.PLAYING && this.board.moveTile(row, column);
    }

    @Override
    public void newGame() {
        this.board = this.shuffler.shuffle();
    }
}
