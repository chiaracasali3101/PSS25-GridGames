package it.unibo.gridgames.model.game2048;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LineMergerImplTest {

    private static final int FOUR = 4;
    private static final int EIGHT = 8;
    private static final int SIXTEEN = 16;

    private final LineMerger merger = new LineMergerImpl();

    @Test
    void emptyLineStaysEmpty() {
        final MergeResult result = merger.merge(new int[] {0, 0, 0, 0});

        assertArrayEquals(new int[] {0, 0, 0, 0}, result.line());
        assertEquals(0, result.points());
    }

    @Test
    void tilesSlideToTheLeft() {
        final MergeResult result = merger.merge(new int[] {0, 2, 0, 4});

        assertArrayEquals(new int[] {2, 4, 0, 0}, result.line());
        assertEquals(0, result.points());
    }

    @Test
    void differentValuesDoNotMerge() {
        final MergeResult result = merger.merge(new int[] {2, 4, 8, 16});

        assertArrayEquals(new int[] {2, 4, 8, 16}, result.line());
        assertEquals(0, result.points());
    }

    @Test
    void equalNeighboursMerge() {
        final MergeResult result = merger.merge(new int[] {2, 2, 4, 0});

        assertArrayEquals(new int[] {4, 4, 0, 0}, result.line());
        assertEquals(FOUR, result.points());
    }

    @Test
    void equalTilesSeparatedByGapsMerge() {
        final MergeResult result = merger.merge(new int[] {2, 0, 0, 2});

        assertArrayEquals(new int[] {4, 0, 0, 0}, result.line());
        assertEquals(FOUR, result.points());
    }

    @Test
    void twoPairsMergeSeparately() {
        final MergeResult result = merger.merge(new int[] {2, 2, 2, 2});

        assertArrayEquals(new int[] {4, 4, 0, 0}, result.line());
        assertEquals(EIGHT, result.points());
    }

    @Test
    void mergedTileDoesNotMergeAgain() {
        final MergeResult result = merger.merge(new int[] {4, 4, 8, 0});

        assertArrayEquals(new int[] {8, 8, 0, 0}, result.line());
        assertEquals(EIGHT, result.points());
    }

    @Test
    void oddRunMergesTheLeadingPair() {
        final MergeResult result = merger.merge(new int[] {2, 2, 2, 0});

        assertArrayEquals(new int[] {4, 2, 0, 0}, result.line());
        assertEquals(FOUR, result.points());
    }

    @Test
    void pointsAreTheSumOfMergedTiles() {
        final MergeResult result = merger.merge(new int[] {4, 4, 8, 8});

        assertArrayEquals(new int[] {8, 16, 0, 0}, result.line());
        assertEquals(EIGHT + SIXTEEN, result.points());
    }

    @Test
    void inputLineIsNotModified() {
        final int[] line = {2, 2, 0, 0};

        merger.merge(line);

        assertArrayEquals(new int[] {2, 2, 0, 0}, line);
    }

    @Test
    void resultLineIsACopy() {
        final MergeResult result = merger.merge(new int[] {2, 0, 0, 0});

        result.line()[0] = EIGHT;

        assertArrayEquals(new int[] {2, 0, 0, 0}, result.line());
    }

    @Test
    void nullLineIsRejected() {
        assertThrows(NullPointerException.class, () -> merger.merge(null));
    }
}
