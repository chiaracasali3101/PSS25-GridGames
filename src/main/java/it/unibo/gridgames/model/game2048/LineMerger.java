package it.unibo.gridgames.model.game2048;

/**
 * Slides and merges a single line of tiles towards its first cell.
 * Moves in every direction are reduced to this operation by reading
 * rows and columns in the appropriate order.
 */
@FunctionalInterface
public interface LineMerger {

    /**
     * Pushes all tiles of the line towards index 0, merging pairs of equal
     * neighbours. Each tile takes part in at most one merge per move.
     * The given array is not modified.
     *
     * @param line the cell values, where 0 means empty
     * @return the resulting line and the points gained
     * @throws NullPointerException if line is null
     */
    MergeResult merge(int[] line);
}
