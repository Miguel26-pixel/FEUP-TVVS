package nl.tudelft.jpacman.level;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.Ghost;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


/**
 * Tests various aspects of level.
 *
 * @author Jeroen Roosen 
 */
class LevelTest {

    /**
     * The level under test.
     */
    private Level level;


    /**
     * The level under test.
     */
    private LevelFactory levelfactory;

        /**
     * The map parser under test.
     */
    private MapParser mapParser;

    /**
     * An NPC on this level.
     */
    private final Ghost ghost = mock(Ghost.class);

    /**
     * Starting position 1.
     */
    private final Square square1 = mock(Square.class);

    /**
     * Starting position 2.
     */
    private final Square square2 = mock(Square.class);

    /**
     * The board for this level.
     */
    private final Board board = mock(Board.class);

    /**
     * The collision map.
     */
    private final CollisionMap collisions = mock(CollisionMap.class);

    private final List<Ghost> ghosts;

    private final List<Square> startSpots;

    /**
     * Sets up the level with the default board, a single NPC and a starting
     * square.
     */
    @BeforeEach
    void setUp() {
        final long defaultInterval = 100L;
        level = new Level(board, Lists.newArrayList(ghost), Lists.newArrayList(
            square1, square2), collisions);
        when(ghost.getInterval()).thenReturn(defaultInterval);
    }

    /**
     * Validates the state of the level when it isn't started yet.
     */
    @Test
    void noStart() {
        assertThat(level.isInProgress()).isFalse();
    }

    /**
     * Validates the state of the level when it is stopped without starting.
     */
    @Test
    void stop() {
        level.stop();
        assertThat(level.isInProgress()).isFalse();
    }

    /**
     * Validates the state of the level when it is started.
     */
    @Test
    void start() {
        level.start();
        assertThat(level.isInProgress()).isTrue();
    }

    /**
     * Validates the state of the level when it is started then stopped.
     */
    @Test
    void startStop() {
        level.start();
        level.stop();
        assertThat(level.isInProgress()).isFalse();
    }

    /**
     * Verifies registering a player puts the player on the correct starting
     * square.
     */
    @Test
    void registerPlayer() {
        Player p = mock(Player.class);
        level.registerPlayer(p);
        verify(p).occupy(square1);
    }

    /**
     * Verifies registering a player twice does not do anything.
     */
    @Test
    void registerPlayerTwice() {
        Player p = mock(Player.class);
        level.registerPlayer(p);
        level.registerPlayer(p);
        verify(p, times(1)).occupy(square1);
    }

    /**
     * Verifies registering a second player puts that player on the correct
     * starting square.
     */
    @Test
    void registerSecondPlayer() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        level.registerPlayer(p1);
        level.registerPlayer(p2);
        verify(p2).occupy(square2);
    }

    /**
     * Verifies registering a third player puts the player on the correct
     * starting square.
     */
    @Test
    void registerThirdPlayer() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        Player p3 = mock(Player.class);
        level.registerPlayer(p1);
        level.registerPlayer(p2);
        level.registerPlayer(p3);
        verify(p3).occupy(square1);
    }

    @Test
    public void testMakeGhostSquareNull() {
        try {
            mapParser.makeGhostSquare(null,ghost);
        } catch (Exception e) {
            System.err.println("Error trying to make ghost square");
        }
    }

    @Test
    public void testMakeGhostSquareNullGhost() {
        try {
            mapParser.makeGhostSquare(ghosts,null);
        } catch (Exception e) {
            System.err.println("Error trying to make ghost square");
        }
    }

    @Test
    public void testMakeGhostSquareNullGhosts() {
        try {
            mapParser.makeGhostSquare(new Ghost[] { ghost, null }, ghost);
        } catch (Exception e) {
            System.err.println("Error trying to make ghost square");
        }
    }

    @Test
    public void testMakeGhostSquareAllNullGhosts() {
        try {
            mapParser.makeGhostSquare(new Ghost[] { null, null }, ghost);
        } catch (Exception e) {
            System.err.println("Error trying to make ghost square");
        }
    }

    @Test
    public void testMakeGhostSquareEmpty() {
        assertThat(mapParser.makeGhostSquare(new Ghost[] {}, ghost)).isInstanceOf(Square.class);
    }


    @Test
    public void testMakeGhostSquare() {
        assertThat(mapParser.makeGhostSquare(ghosts, ghost)).isInstanceOf(Square.class);
    }


    @Test
    public void testCreateLevelNullGhost() {
        try {
            levelfactory.createLevel(board, new Ghost[] { ghost, null }, new Square[] { square1, square2 });
        } catch (Exception e) {
            System.err.println("Error trying to create level");
        }
    }

    @Test
    public void testCreateLevelNullStart() {
        try {
            levelfactory.createLevel(board, ghosts, new Square[] { square1, null });
        } catch (Exception e) {
            System.err.println("Error trying to create level");
        }
    }

    @Test
    public void testCreateLevelNullBoard() {
        try {
            levelfactory.createLevel(null, ghosts, startSpots);
        } catch (Exception e) {
            System.err.println("Error trying to create level");
        }
    }

    @Test
    public void testCreateLevelNullGhosts() {
        try {
            levelfactory.createLevel(board, null, new Square[] { square1 });
        } catch (Exception e) {
            System.err.println("Error trying to create level");
        }
        
    }

    @Test
    public void testCreateLevelNullStarts() {
        try {
            levelfactory.createLevel(board, ghosts, null);
        } catch (Exception e) {
            System.err.println("Error trying to create level");
        }
    }

    @Test
    public void testCreateLevel() {
        assertThat(levelfactory.createLevel(board, ghosts, ghost)).isInstanceOf(Level.class);
    }


}
