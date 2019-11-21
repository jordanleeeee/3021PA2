package views;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * Helper class for a {@link VBox} with "big-vbox" style applied.
 */
public class BigVBox extends VBox {

    /**
     * Creates an instance with spacing of 20.
     */
    public BigVBox() {
        // TODO
        this(20);
    }

    public BigVBox(double spacing) {
        super(spacing);
    }

    public BigVBox(Node... children) {
        super(children);
    }

    public BigVBox(double spacing, Node... children) {
        super(spacing, children);
    }

    {
        // TODO: Add "big-vbox" style
        //setStyle("-fx-background-color: #ddd; -fx-alignment: center; -fx-spacing: 20;");
        //setStyle("-fx-background-color: #ddd; -fx-spacing: 20;");
    }
}
