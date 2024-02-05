package nl.tudelft.jpacman.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import nl.tudelft.jpacman.sprite.PacManSprites;

import java.util.Arrays;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.npc.ghost.Blinky;
import nl.tudelft.jpacman.npc.ghost.Inky;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.sprite.Sprite;



/**
 * Tests the linking of squares done by the board factory.
 *
 * @author Jeroen Roosen 
 */
class BoardPanelTest {

    Launcher launcher = new Launcher();

    BoardPanel boardPanel = new BoardPanel(launcher.makeGame());


    @Test
    public void testNullGame() {
        Game game = null;
        assertThrows(AssertionError.class, () -> {
            BoardPanel boardPanel = new BoardPanel(game);
        });
    }


    @Test
    public void testNullGraphicsOnPaint() {
        Graphics g = null;
        assertThrows(AssertionError.class, () -> {
            boardPanel.paint(g);
        });
    }


    @Test
    public void testNoNearestPlayer() {
        Map<Direction, Sprite> spriteMap = new HashMap<>();
        Ghost g = new Blinky(spriteMap);
        Square s = mock(Square.class);
        Mockito.when(s.isAccessibleTo(g)).thenReturn(false);
        List<Unit> l = new ArrayList<>();
        l.add(g);
        Mockito.when(s.getOccupants()).thenReturn(l);
        Mockito.when(s.getSquareAt(any(Direction.class))).thenReturn(s);
        g.occupy(s);

        g.nextAiMove();
        assertThat(g.nextMove()).isNull();
    }

    @Test
    public void testNearestPlayerWithoutSquare() {
        Map<Direction, Sprite> spriteMap = new HashMap<>();
        Ghost g = new Blinky(spriteMap);
        g.leaveSquare();

        Throwable exception = assertThrows(AssertionError.class, g::nextAiMove);
    }

    @Test
    public void testNearestPlayerNoDirection() {
        Map<Direction, Sprite> spriteMap = new HashMap<>();
        Ghost g = new Inky(spriteMap);
        Square s = mock(Square.class);
        Mockito.when(s.isAccessibleTo(g)).thenReturn(false);
        List<Unit> l = new ArrayList<>();
        l.add(g);
        Mockito.when(s.getOccupants()).thenReturn(l);
        Mockito.when(s.getSquareAt(any(Direction.class))).thenReturn(s);
        g.occupy(s);

        assertThat(g.nextMove()).isNull();
    }
}