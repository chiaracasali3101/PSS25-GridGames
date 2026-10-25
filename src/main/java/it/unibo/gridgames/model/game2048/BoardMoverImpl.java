package it.unibo.gridgames.model.game2048;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Implementation of {@link BoardMover} that reduces every direction to a
 * {@link LineMerger} call on the cells read in the right order.
 */
public final class BoardMoverImpl implements BoardMover {

    private final LineMerger merger;

    /**
     * Creates a mover that uses the standard 2048 merging rules.
     */
    public BoardMoverImpl() {
        this(new LineMergerImpl());
    }

    /**
     * Creates a mover that uses the given merging rules.
     *
     * @param merger the strategy used to slide and merge a single line
     */
    public BoardMoverImpl(final LineMerger merger) {
        this.merger = Objects.requireNonNull(merger);
    }

    @Override
    public MoveResult move(final Board board, final Direction direction) {
        Objects.requireNonNull(board);
        Objects.requireNonNull(direction);
        final int size = board.getSize();
        boolean changed = false;
        int points = 0;
        for (int line = 0; line < size; line++) {
            final List<Coordinate> cells = lineCells(line, size, direction);
            final int[] before = cells.stream()
                .mapToInt(c -> board.getValue(c.row(), c.column()))
                .toArray();
            final MergeResult result = this.merger.merge(before);
            final int[] after = result.line();
            for (int position = 0; position < size; position++) {
                final Coordinate cell = cells.get(position);
                board.setValue(cell.row(), cell.column(), after[position]);
            }
            changed = changed || !Arrays.equals(before, after);
            points += result.points();
        }
        return new MoveResult(changed, points);
    }

    // celle della linea partendo dal lato verso cui si spinge (es. RIGHT legge la riga al contrario)
    private static List<Coordinate> lineCells(final int line, final int size, final Direction direction) {
        final int last = size - 1;
        return IntStream.range(0, size)
            .mapToObj(position -> switch (direction) {
                case LEFT -> new Coordinate(line, position);
                case RIGHT -> new Coordinate(line, last - position);
                case UP -> new Coordinate(position, line);
                case DOWN -> new Coordinate(last - position, line);
            })
            .toList();
    }
}
