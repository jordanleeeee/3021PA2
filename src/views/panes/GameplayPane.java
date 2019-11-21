package views.panes;

import controllers.AudioManager;
import controllers.LevelManager;
import controllers.SceneManager;
import io.Deserializer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.FXGame;
import org.jetbrains.annotations.NotNull;
import views.BigButton;
import views.BigVBox;
import views.GameplayInfoPane;

import java.io.FileNotFoundException;

import static models.Config.TILE_SIZE;

/**
 * Pane for displaying the actual gameplay.
 */
public class GameplayPane extends GamePane {

    private HBox topBar = new HBox(20);
    private VBox canvasContainer = new BigVBox();
    private Canvas gameplayCanvas = new Canvas();
    private HBox bottomBar = new HBox(20);
    private Canvas queueCanvas = new Canvas();
    private Button quitToMenuButton = new BigButton("Quit to menu");

    private FXGame game;

    private final IntegerProperty ticksElapsed = new SimpleIntegerProperty();
    private GameplayInfoPane infoPane = null;

    public GameplayPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void connectComponents() {
        // TODO
        infoPane = new GameplayInfoPane(new SimpleStringProperty("Generate"), new SimpleIntegerProperty(0), new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        canvasContainer.getChildren().add(infoPane);
        canvasContainer.getChildren().add(gameplayCanvas);
        topBar.getChildren().add(canvasContainer);
        this.setTop(topBar);
        bottomBar.getChildren().add(queueCanvas);
        bottomBar.getChildren().add(quitToMenuButton);
        this.setBottom(bottomBar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        gameplayCanvas.setOnMouseClicked((e)->onCanvasClicked(e));
        quitToMenuButton.setOnAction((e)->doQuitToMenuAction());
    }

    /**
     * Handles events when somewhere on the {@link GameplayPane#gameplayCanvas} is clicked.
     *
     * @param event Event to handle.
     */
    private void onCanvasClicked(MouseEvent event) {
        // TODO
    }

    /**
     * Handles events when a key is pressed.
     *
     * @param event Event to handle.
     */
    private void onKeyPressed(KeyEvent event) {
        // TODO
        if(event.getCode()== KeyCode.U){
            //undo
            game.undoStep();
        }
        if(event.getCode()== KeyCode.S){
            //skip pipe
            game.skipPipe();
        }
    }

    /**
     * Creates a popup which tells the player they have completed the map.
     */
    private void createWinPopup() {
        // TODO
        ButtonType back = new ButtonType("Return");
        ButtonType next = new ButtonType("Next Map");
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Level Cleared!", next, back);
        a.showAndWait().ifPresent(type -> {
            if (type == back) {
                SceneManager.getInstance().showPane(null);
            }
            else{
                loadNextMap();
            }
        });
    }

    /**
     * Loads the next map in the series, or generate a new map if one is not available.
     */
    private void loadNextMap() {
        // TODO

    }

    /**
     * Creates a popup which tells the player they have lost the map.
     */
    private void createLosePopup() {
        // TODO
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "You lose!", new ButtonType("Return"));
        a.show();
    }

    /**
     * Creates a popup which prompts the player whether they want to quit.
     */
    private void doQuitToMenuAction() {
        // TODO
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,"return to menue?", ButtonType.OK, ButtonType.CANCEL);
        a.setContentText("game status will lose...");
        a.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                doQuitToMenu();
            }
            else{
                a.close();
            }
        });
    }

    /**
     * Go back to the Level Select scene.
     */
    private void doQuitToMenu() {
        // TODO
        SceneManager.getInstance().showPane(LevelSelectPane.class);
    }

    /**
     * Starts a new game with the given name.
     *
     * @param game New game to start.
     */
    void startGame(@NotNull FXGame game) {
        // TODO
        this.game = game;

        if(this.game.hasWon()){
            createWinPopup();
        }
        if(this.game.hasLost()){
            endGame();
        }
    }

    /**
     * Cleans up the currently bound game.
     */
    private void endGame() {
        // TODO
        createLosePopup();
    }
}
