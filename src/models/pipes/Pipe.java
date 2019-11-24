package models.pipes;

import controllers.Renderer;
import controllers.ResourceLoader;
import javafx.scene.image.Image;
import models.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

import java.util.Arrays;

public class Pipe implements MapElement {

    @NotNull
    private final Shape shape;
    private boolean filled = false;

    /**
     * Creates a new pipe with a given shape.
     *
     * @param shape Shape of the pipe.
     */
    public Pipe(@NotNull Shape shape) {
        this.shape = shape;
    }

    /**
     * Sets the pipe as filled.
     */
    public void setFilled() {
        filled = true;
    }

    /**
     * @return Whether this pipe is filled.
     */
    public boolean getFilled() {
        return filled;
    }

    /**
     * @return List of connections for this pipe.
     * @throws IllegalStateException if {@code this} pipe cannot be identified.
     */
    @NotNull
    public Direction[] getConnections() {
        switch (shape) {
            case HORIZONTAL:
                return new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL:
                return new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT:
                return new Direction[]{Direction.UP, Direction.LEFT};
            case TOP_RIGHT:
                return new Direction[]{Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT:
                return new Direction[]{Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT:
                return new Direction[]{Direction.DOWN, Direction.RIGHT};
            case CROSS:
                return Direction.values();
            default:
                throw new IllegalStateException("Unknown shape");
        }
    }

    /**
     * @return The character representation of this pipe. Note that the representation is different for filled and
     * unfilled pipes.
     */
    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    /**
     * {@inheritDoc}
     *
     * @return The image representation of this pipe. Note that the representation is different for filled and unfilled
     * pipes.
     */
    @NotNull
    @Override
    public Renderer.CellImage getImageRep() {
        return shape.getCellImage(filled);
    }

    /**
     * Converts a String to a Pipe.
     *
     * <p>
     * Refer to README for the list of ASCII representation to the pipes.
     * </p>
     *
     * @param rep String representation of the pipe.
     * @return Pipe identified by the string.
     * @throws IllegalArgumentException if the String does not represent a known pipe.
     */
    @NotNull
    public static Pipe fromString(@NotNull String rep) {
        String actualRep = rep.trim();

        return Arrays.stream(Shape.values())
                .filter(shape -> shape.rep.equals(actualRep))
                .findAny()
                .map(Pipe::new)
                .orElseThrow(() -> new IllegalArgumentException("Unknown shape"));
    }

    @NotNull
    @Override
    public String toSerializedRep() {
        return shape.rep;
    }

    /**
     * Helper class for different pipe shapes.
     */
    public enum Shape {
        HORIZONTAL("HZ", PipePatterns.Filled.HORIZONTAL, PipePatterns.Unfilled.HORIZONTAL),
        VERTICAL("VT", PipePatterns.Filled.VERTICAL, PipePatterns.Unfilled.VERTICAL),
        TOP_LEFT("TL", PipePatterns.Filled.TOP_LEFT, PipePatterns.Unfilled.TOP_LEFT),
        TOP_RIGHT("TR", PipePatterns.Filled.TOP_RIGHT, PipePatterns.Unfilled.TOP_RIGHT),
        BOTTOM_LEFT("BL", PipePatterns.Filled.BOTTOM_LEFT, PipePatterns.Unfilled.BOTTOM_LEFT),
        BOTTOM_RIGHT("BR", PipePatterns.Filled.BOTTOM_RIGHT, PipePatterns.Unfilled.BOTTOM_RIGHT),
        CROSS("CR", PipePatterns.Filled.CROSS, PipePatterns.Unfilled.CROSS);

        private static final Image CORNER_UNFILLED = new Image(ResourceLoader.getResource("assets/images/top-left-unfilled.png"));
        private static final Image CORNER_FILLED = new Image(ResourceLoader.getResource("assets/images/top-left-filled.png"));
        private static final Image CROSS_UNFILLED = new Image(ResourceLoader.getResource("assets/images/cross-unfilled.png"));
        private static final Image CROSS_FILLED = new Image(ResourceLoader.getResource("assets/images/cross-filled.png"));
        private static final Image STRAIGHT_UNFILLED = new Image(ResourceLoader.getResource("assets/images/vert-unfilled.png"));
        private static final Image STRAIGHT_FILLED = new Image(ResourceLoader.getResource("assets/images/vert-filled.png"));

        @NotNull
        final String rep;
        final char filledChar;
        final char unfilledChar;

        Shape(@NotNull String rep, char filled, char unfilled) {
            this.rep = rep;
            this.filledChar = filled;
            this.unfilledChar = unfilled;
        }

        char getCharByState(boolean isFilled) {
            return isFilled ? filledChar : unfilledChar;
        }

        /**
         * Retrieves the image representation of the pipe.
         *
         * @param isFilled Whether the pipe is filled.
         * @return Image representation of the pipe.
         * @throws IllegalStateException When {@code this} is not a known pipe shape.
         */
        @NotNull Renderer.CellImage getCellImage(boolean isFilled) {
            // TODO
            if(isFilled){
                switch (filledChar){
                    case PipePatterns.Filled.HORIZONTAL: return new Renderer.CellImage(STRAIGHT_FILLED, 90);
                    case PipePatterns.Filled.VERTICAL: return new Renderer.CellImage(STRAIGHT_FILLED, 0);
                    case PipePatterns.Filled.TOP_LEFT: return new Renderer.CellImage(CORNER_FILLED, 0);
                    case PipePatterns.Filled.TOP_RIGHT: return new Renderer.CellImage(CORNER_FILLED, 90);
                    case PipePatterns.Filled.BOTTOM_LEFT: return new Renderer.CellImage(CORNER_FILLED, 270);
                    case PipePatterns.Filled.BOTTOM_RIGHT: return new Renderer.CellImage(CORNER_FILLED, 180);
                    case PipePatterns.Filled.CROSS: return new Renderer.CellImage(CROSS_FILLED, 0);
                    default: throw new IllegalStateException();
                }
            }
            else{
                switch (unfilledChar){
                    case PipePatterns.Unfilled.HORIZONTAL: return new Renderer.CellImage(STRAIGHT_UNFILLED, 90);
                    case PipePatterns.Unfilled.VERTICAL: return new Renderer.CellImage(STRAIGHT_UNFILLED, 0);
                    case PipePatterns.Unfilled.TOP_LEFT: return new Renderer.CellImage(CORNER_UNFILLED, 0);
                    case PipePatterns.Unfilled.TOP_RIGHT: return new Renderer.CellImage(CORNER_UNFILLED, 90);
                    case PipePatterns.Unfilled.BOTTOM_LEFT: return new Renderer.CellImage(CORNER_UNFILLED, 270);
                    case PipePatterns.Unfilled.BOTTOM_RIGHT: return new Renderer.CellImage(CORNER_UNFILLED, 180);
                    case PipePatterns.Unfilled.CROSS: return new Renderer.CellImage(CROSS_UNFILLED, 0);
                    default: throw new IllegalStateException();
                }
            }
        }
    }
}
