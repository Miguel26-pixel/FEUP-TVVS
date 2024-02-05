package nl.tudelft.jpacman.sprite;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.Mockito.mock;
import nl.tudelft.jpacman.sprite.ImageSprite;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Board;

/**
 * Verifies the loading of sprites.
 *
 * @author Jeroen Roosen 
 */
@SuppressWarnings("magicnumber")
public class SpriteTest {

    private Sprite sprite;
    private SpriteStore store;
    private ImageSprite imageSprite;
    private Image image;
    private final Graphics graphics;
    private static final int SPRITE_SIZE = 64;


    private static final int MAX_WIDTH = 2;
    private static final int MAX_HEIGHT = 3;

    private final Square[][] grid = {
        { mock(Square.class), mock(Square.class), mock(Square.class) },
        { mock(Square.class), mock(Square.class), mock(Square.class) },
    };
    private final Board board = new Board(grid);

    /**
     * The common fixture of this test class is
     * a 64 by 64 pixel white sprite.
     *
     * @throws java.io.IOException
     *      when the sprite could not be loaded.
     */
    @BeforeEach
    public void setUp() throws IOException {
        store = new SpriteStore();
        sprite = store.loadSprite("/sprite/64x64white.png");
    }

    /**
     * Verifies the width of a static sprite.
     */
    @Test
    public void spriteWidth() {
        assertThat(sprite.getWidth()).isEqualTo(SPRITE_SIZE);
    }

    /**
     * Verifies the height of a static sprite.
     */
    @Test
    public void spriteHeight() {
        assertThat(sprite.getHeight()).isEqualTo(SPRITE_SIZE);
    }

    /**
     * Verifies that an IOException is thrown when the resource could not be
     * loaded.
     *
     * @throws java.io.IOException
     *             since the sprite cannot be loaded.
     */
    @Test
    public void resourceMissing() throws IOException {
        assertThatThrownBy(() -> store.loadSprite("/sprite/nonexistingresource.png"))
            .isInstanceOf(IOException.class);
    }

    /**
     * Verifies that an animated sprite is correctly cut from its base image.
     */
    @Test
    public void animationWidth() {
        AnimatedSprite animation = store.createAnimatedSprite(sprite, 4, 0,
            false);
        assertThat(animation.getWidth()).isEqualTo(16);
    }

    /**
     * Verifies that an animated sprite is correctly cut from its base image.
     */
    @Test
    public void animationHeight() {
        AnimatedSprite animation = store.createAnimatedSprite(sprite, 4, 0,
            false);
        assertThat(animation.getHeight()).isEqualTo(64);
    }

    /**
     * Verifies that an split sprite is correctly cut from its base image.
     */
    @Test
    public void splitWidth() {
        Sprite split = sprite.split(10, 11, 12, 13);
        assertThat(split.getWidth()).isEqualTo(12);
    }

    /**
     * Verifies that an split sprite is correctly cut from its base image.
     */
    @Test
    public void splitHeight() {
        Sprite split = sprite.split(10, 11, 12, 13);
        assertThat(split.getHeight()).isEqualTo(13);
    }

    /**
     * Verifies that a split that isn't within the actual sprite returns an empty sprite.
     */
    @Test
    public void splitOutOfBounds() {
        Sprite split = sprite.split(10, 10, 64, 10);
        assertThat(split).isInstanceOf(EmptySprite.class);
    }


    /**
     * Verify that squares at key positions are properly set.
     * @param x Horizontal coordinate of relevant cell.
     * @param y Vertical coordinate of relevant cell.
     */
    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "0, 1",
    })
    public void testSpriteDrawXY(int x, int y) {
        try {
            imageSprite.draw(graphics, x, y, 1, 1);
        } catch (Exception e) {
            System.err.println("Error trying to draw");
        }
    }
    /**
     * Verify that squares at key positions are properly set.
     * @param x Horizontal coordinate of relevant cell.
     * @param y Vertical coordinate of relevant cell.
     */
    @ParameterizedTest
    @CsvSource({
            "-1, -1",
            "0, -1",
            "-1, 0",
    })
    public void testSpriteDrawXYNegative(int x, int y) {
        try {
            imageSprite.draw(graphics, x, y, 1, 1);
        } catch (Exception e) {
            System.err.println("Error trying to draw");
        }
    }

    /**
     * Verify that squares at key positions are properly set.
     * @param x Horizontal coordinate of relevant cell.
     * @param y Vertical coordinate of relevant cell.
     */
    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "1, 2",
    })
    public void testSpriteDrawWidthHeight(int width, int height) {
        try {
            imageSprite.draw(graphics, 0, 0, width, height);
        } catch (Exception e) {
            System.err.println("Error trying to draw");
        }
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
    public void testSpriteDrawWidthHeightNegative(int width, int height) {
        try {
            board.squareAt(width, height);
        } catch (Exception e) {
            System.err.println("Error trying to draw");
        }
    }

        /**
     * Verify that squares at key positions are properly set.
     * @param x Horizontal coordinate of relevant cell.
     * @param y Vertical coordinate of relevant cell.
     */
    @Test
    public void testSpriteDrawNullGraphic() {
        try {
            imageSprite.draw(null, 0, 0, 1, 1);
        } catch (Exception e) {
            System.err.println("Error trying to draw");
        }
    }

            /**
     * Verify that squares at key positions are properly set.
     * @param x Horizontal coordinate of relevant cell.
     * @param y Vertical coordinate of relevant cell.
     */
    @Test
    public void testSpriteDrawWidthHeightMAX() {
        try {
            imageSprite.draw(graphics, 0, 0, MAX_WIDTH, MAX_HEIGHT);
        } catch (Exception e) {
            System.err.println("Error trying to draw");
        }
    }


                /**
     * Verify that squares at key positions are properly set.
     * @param x Horizontal coordinate of relevant cell.
     * @param y Vertical coordinate of relevant cell.
     */
    @Test
    public void testSpriteDrawWidthHeightNegative() {
        try {
            imageSprite.draw(graphics, 0, 0, -MAX_WIDTH, -MAX_HEIGHT);
        } catch (Exception e) {
            System.err.println("Error trying to draw");
        }
    }
}
