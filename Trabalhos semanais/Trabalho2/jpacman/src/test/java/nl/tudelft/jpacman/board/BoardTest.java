package nl.tudelft.jpacman.board;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

/**
 * Test various aspects of board.
 *
 * @author Jeroen Roosen 
 */
class BoardTest {

    private static final int MAX_WIDTH = 2;
    private static final int MAX_HEIGHT = 3;

    private final Square[][] grid = {
        { mock(Square.class), mock(Square.class), mock(Square.class) },
        { mock(Square.class), mock(Square.class), mock(Square.class) },
    };
    private final Board board = new Board(grid);

    /**
     * Verifies the board has the correct width.
     */
    @Test
    void verifyWidth() {
        assertThat(board.getWidth()).isEqualTo(MAX_WIDTH);
    }

    /**
     * Verifies the board has the correct height.
     */
    @Test
    void verifyHeight() {
        assertThat(board.getHeight()).isEqualTo(MAX_HEIGHT);
    }

    /**
     * Verify that squares at key positions are properly set.
     * @param x Horizontal coordinate of relevant cell.
     * @param y Vertical coordinate of relevant cell.
     */
    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "1, 2",
            "0, 1",
            "1, 1",
            "1, 0",
            "0, 2"
    })
    void testSquareAt(int x, int y) {
        assertThat(board.squareAt(x, y)).isEqualTo(grid[x][y]);
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 0",
            "0, -1",
            "0, -1",
            "-1, -1",
            "-1, 0",
            "-1, 2"
    })
    public void testSquareAtNegative(int x, int y) {
        try {
            board.squareAt(x, y);
        } catch (Exception e) {
            System.err.println("Error trying to make ghost square");
        }
    }

    @Test
    public void testSquareAtMAX() {
        try {
            board.squareAt(MAX_WIDTH, MAX_HEIGHT);
        } catch (Exception e) {
            System.err.println("Error trying to make ghost square");
        }
    }
}
