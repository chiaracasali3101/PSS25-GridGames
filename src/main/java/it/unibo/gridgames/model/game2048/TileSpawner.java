package it.unibo.gridgames.model.game2048;

import java.util.Optional;

/**
 * Places new tiles on a 2048 board.
 */
@FunctionalInterface
public interface TileSpawner {

    /**
     * Puts a new tile in one of the empty cells of the board.
     *
     * @param board the board to place the tile on
     * @return the cell where the tile was placed, or an empty optional if the board is full
     * @throws NullPointerException if board is null
     */
    Optional<Coordinate> spawn(Board board);
}
