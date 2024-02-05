package nl.tudelft.jpacman.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import nl.tudelft.jpacman.sprite.PacManSprites;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Tests the linking of squares done by the board factory.
 *
 * @author Jeroen Roosen 
 */
class BoardFactoryTest {

    /**
     * The factory under test.
     */
    private BoardFactory factory;

    /**
     * Squares on the board to test.
     */
    private Square s1, s2;

    private final Square[][] grid = {
        { mock(Square.class), mock(Square.class), mock(Square.class) },
        { mock(Square.class), mock(Square.class), mock(Square.class) },
    };
    private final Board board = new Board(grid);

    /**
     * Resets the factory under test.
     */
    @BeforeEach
    void setUp() {
        PacManSprites sprites = mock(PacManSprites.class);
        factory = new BoardFactory(sprites);

        s1 = new BasicSquare();
        s2 = new BasicSquare();
    }

    /**
     * Verifies that a single cell is connected to itself on all sides.
     */
    @Test
    void worldIsRound() {
        factory.createBoard(new Square[][]{{s1}});
        assertThat(Arrays.stream(Direction.values()).map(s1::getSquareAt)).containsOnly(s1);
    }

    /**
     * Verifies a chain of cells is connected to the east.
     */
    @Test
    void connectedEast() {
        factory.createBoard(new Square[][]{{s1}, {s2}});
        assertThat(s1.getSquareAt(Direction.EAST)).isEqualTo(s2);
        assertThat(s2.getSquareAt(Direction.EAST)).isEqualTo(s1);
    }

    /**
     * Verifies a chain of cells is connected to the west.
     */
    @Test
    void connectedWest() {
        factory.createBoard(new Square[][]{{s1}, {s2}});
        assertThat(s1.getSquareAt(Direction.WEST)).isEqualTo(s2);
        assertThat(s2.getSquareAt(Direction.WEST)).isEqualTo(s1);
    }

    /**
     * Verifies a chain of cells is connected to the north.
     */
    @Test
    void connectedNorth() {
        factory.createBoard(new Square[][]{{s1, s2}});
        assertThat(s1.getSquareAt(Direction.NORTH)).isEqualTo(s2);
        assertThat(s2.getSquareAt(Direction.NORTH)).isEqualTo(s1);
    }

    /**
     * Verifies a chain of cells is connected to the south.
     */
    @Test
    void connectedSouth() {
        factory.createBoard(new Square[][]{{s1, s2}});
        assertThat(s1.getSquareAt(Direction.SOUTH)).isEqualTo(s2);
        assertThat(s2.getSquareAt(Direction.SOUTH)).isEqualTo(s1);
    }


    /**
     * Verifies a empty board can be created
     */
    @Test
    void createBoardEmptyTest() {
        assertThat(factory.createBoard(new Square[][]{{}})).isInstanceOf(Board.class);
    }

    /**
     * Verifies a empty board can be created
     */
    @Test
    void createBoardTest() {
        assertThat(factory.createBoard(new Square[][]{{s1, s2}})).isInstanceOf(Board.class);
    }

    @Test
    public void testNullGrid() {
        Square[][] grid = null;
        assertThrows(AssertionError.class, () -> {
            Board board = factory.createBoard(grid);
            assertNotNull(board);
        });
    }

        /**
     * Verifies a empty board can be created
     */
    // @Test
    // void createBoardTest(Square[][] grid) {
    //     if (grid == null) {
    //         throw new IllegalArgumentException("Board can't be null.");
    //     }
    //     assertThat(factory.createBoard(grid)).isInstanceOf(Board.class);
    // }

/*     @Test
    public void createBoardTestNull() {
        try {
            factory.createBoard(null);
        } catch (Exception e) {
            System.err.println("Error trying to create board");
        }
    }

    @Test
    public void createBoardTestAllNull() {
        try {
            factory.createBoard(new Square[][]{{null, null}});
        } catch (Exception e) {
            System.err.println("Error trying to  create board");
        }
    }

    @Test
    public void createBoardTestOneNull() {
        try {
            factory.createBoard(new Square[][]{{null, s2}});
        } catch (Exception e) {
            System.err.println("Error trying to create board");
        }
    } */
}
