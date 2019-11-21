package views;

import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Helper class for a {@link Button} with "big-button" style applied.
 */
public class BigButton extends Button {

    public BigButton() {
        super();
    }

    public BigButton(String text) {
        super(text);
    }

    public BigButton(String text, Node graphic) {
        super(text, graphic);
    }

    {
        // TODO: Add "big-button" style
        super.setStyle("-fx-font-size: 15; -fx-font-family: sans-serif;");
        String normalStyle = "-fx-font-size: 15; -fx-font-family: sans-serif; -fx-pref-width: 200; -fx-pref-height: 40; -fx-background-radius: 16px;";
        String hoverStyle = "-fx-background-color: rgb(120, 120, 120); -fx-text-fill: white;";
        setStyle(normalStyle);
        setOnMouseEntered(e -> setStyle(hoverStyle+normalStyle));
        setOnMouseExited(e -> setStyle(normalStyle));
    }

}
