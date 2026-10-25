package it.unibo.gridgames.model.game2048;

/**
 * Moves all tiles of a 2048 board in a direction, merging equal neighbours.
 */
@FunctionalInterface
public interface BoardMover {

    /**
     * Slides and merges every row or column of the board towards the given direction.
     * The board is modified in place; if the move is not valid it is left unchanged.
     *
     * @param board the board to move
     * @param direction the direction the tiles are pushed to
     * @return whether the board changed and the points gained
     * @throws NullPointerException if board or direction is null
     */
    MoveResult move(Board board, Direction direction);
}
