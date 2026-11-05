package it.unibo.gridgames.model.game2048;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Standard implementation of {@link LineMerger} following the 2048 rules.
 */
public final class LineMergerImpl implements LineMerger {

    private static final int EMPTY = 0;

    @Override
    public MergeResult merge(final int[] line) {
        final int[] tiles = Arrays.stream(Objects.requireNonNull(line))
            .filter(value -> value != EMPTY)
            .toArray();
        final IntStream.Builder result = IntStream.builder();
        int points = 0;
        int index = 0;
        // dopo una fusione salto entrambe le tessere, così [4, 4, 8] diventa [8, 8] e non [16]
        while (index < tiles.length) {
            if (index + 1 < tiles.length && tiles[index] == tiles[index + 1]) {
                final int merged = tiles[index] * 2;
                result.add(merged);
                points += merged;
                index += 2;
            } else {
                result.add(tiles[index]);
                index++;
            }
        }
        return new MergeResult(Arrays.copyOf(result.build().toArray(), line.length), points);
    }
}
