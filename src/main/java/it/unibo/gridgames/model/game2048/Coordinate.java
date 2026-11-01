package it.unibo.gridgames.model.game2048;

/**
 * An immutable pair of row and column identifying a cell of a 2048 board.
 *
 * @param row the row index, starting from 0
 * @param column the column index, starting from 0
 */
public record Coordinate(int row, int column) { }
