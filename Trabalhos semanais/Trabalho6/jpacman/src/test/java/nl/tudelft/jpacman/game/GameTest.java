package nl.tudelft.jpacman.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import org.mockito.Mockito;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.npc.ghost.Pinky;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.level.Level;

import java.lang.reflect.Method;

import nl.tudelft.jpacman.level.CollisionInteractionMap;
import nl.tudelft.jpacman.level.Player;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.lang.reflect.InvocationTargetException;

/**
 * Tests various aspects of level.
 *
 * @author Jeroen Roosen 
 */
class GameTest {

    Launcher launcher = new Launcher();

    Game game;

    Game game2;

    private final Player player = mock(Player.class);

    @BeforeEach
    public void setUp() {
        game = Mockito.spy(launcher.makeGame());
        Level level = Mockito.mock(Level.class);
        Mockito.when(game.getLevel()).thenReturn(level);

        game2 = Mockito.spy(launcher.makeGame());
    }

    @Test
    public void testStartNotPlayerAliveORNoPellets() {
        Mockito.when(game.isInProgress()).thenReturn(false);

        game.start();

        Mockito.verify(game.getLevel(), Mockito.times(1)).isAnyPlayerAlive();
        Mockito.verify(game.getLevel(), Mockito.times(0)).remainingPellets();

        assertFalse(game.getLevel().remainingPellets() > 0);
    } 

    @Test
    public void testStartInProgress() {
        Mockito.when(game.isInProgress()).thenReturn(true);

        game.start();

        Mockito.verify(game.getLevel(), Mockito.times(0)).isAnyPlayerAlive();
        Mockito.verify(game.getLevel(), Mockito.times(0)).remainingPellets();
    }

    @Test
    public void testStartNotInProgressAllFine() { 
        assertFalse(game2.isInProgress());

        game2.start();

        assertTrue(game2.getLevel().isAnyPlayerAlive());
        assertTrue(game2.getLevel().remainingPellets() > 0);
    }

}
