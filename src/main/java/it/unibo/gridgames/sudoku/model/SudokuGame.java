package it.unibo.gridgames.sudoku.model;

/**
 * A single Sudoku game: the puzzle, its solution, the errors and the status.
 */
public final class SudokuGame {

    /**
     * Number of errors that makes the player lose.
     */
    public static final int MAX_ERRORS = 3;

    private final Grid solution;
    private final Grid puzzle;
    private final Difficulty difficulty;
    private int emptyCells;
    private int errors;
    private GameStatus status = GameStatus.PLAYING;

    /**
     * Creates a new game with a random puzzle.
     *
     * @param difficulty the difficulty of the puzzle
     */
    public SudokuGame(final Difficulty difficulty) {
        this(difficulty, new SudokuGenerator());
    }

    /**
     * Creates a new game using the given generator.
     *
     * @param difficulty the difficulty of the puzzle
     * @param generator  the generator used to create the puzzle
     */
    public SudokuGame(final Difficulty difficulty, final SudokuGenerator generator) {
        this.difficulty = difficulty;
        this.solution = generator.generateFullGrid();
        this.puzzle = generator.createPuzzle(solution, difficulty);
        this.emptyCells = difficulty.getCellsToRemove();
    }

    /**
     * Tries to write a value in a cell.
     *
     * @param position where the player wants to write
     * @param value    the value, between 1 and 9
     * @return the outcome of the move
     * @throws IllegalArgumentException if the value is not between 1 and 9
     */
    public MoveResult play(final Position position, final int value) {
        if (value < 1 || value > Grid.SIZE) {
            throw new IllegalArgumentException("Value must be between 1 and 9");
        }
        final Cell cell = puzzle.getCell(position);
        if (status != GameStatus.PLAYING || !cell.isEmpty()) {
            return MoveResult.NOT_ALLOWED;
        }
        if (value == solution.getCell(position).getValue()) {
            cell.setValue(value);
            emptyCells--;
            if (emptyCells == 0) {
                status = GameStatus.WON;
            }
            return MoveResult.CORRECT;
        }
        errors++;
        if (errors == MAX_ERRORS) {
            status = GameStatus.LOST;
        }
        return MoveResult.WRONG;
    }

    /**
     * Returns the value shown in a cell.
     *
     * @param position the position of the cell
     * @return the value, or 0 if the cell is empty
     */
    public int getValue(final Position position) {
        return puzzle.getCell(position).getValue();
    }

    /**
     * Tells whether a cell was given at the start of the game.
     *
     * @param position the position of the cell
     * @return true if the player cannot change the cell
     */
    public boolean isFixed(final Position position) {
        return puzzle.getCell(position).isFixed();
    }

    /**
     * Returns the number of errors made so far.
     *
     * @return the number of errors
     */
    public int getErrors() {
        return errors;
    }

    /**
     * Returns the current status of the game.
     *
     * @return playing, won or lost
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Returns the difficulty of this game.
     *
     * @return the difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }
}
