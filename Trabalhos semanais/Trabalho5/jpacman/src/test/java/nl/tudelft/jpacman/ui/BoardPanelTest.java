package nl.tudelft.jpacman.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import nl.tudelft.jpacman.sprite.PacManSprites;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.ui.BoardPanel;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.Launcher;



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



}