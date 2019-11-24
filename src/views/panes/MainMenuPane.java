package views.panes;

import controllers.AudioManager;
import controllers.ResourceLoader;
import controllers.SceneManager;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import views.BigButton;
import views.BigVBox;

/**
 * Pane for displaying the main menu.
 */
public class MainMenuPane extends GamePane {

    @NotNull
    private final VBox container = new BigVBox();
    @NotNull
    private final Label title = new Label("Pipes");
    @NotNull
    private final Button levelSelectButton = new BigButton("Play Game");
    @NotNull
    private final Button levelEditorButton = new BigButton("Level Editor");
    @NotNull
    private final Button settingsButton = new BigButton("About / Settings");
    @NotNull
    private final Button quitButton = new BigButton("Quit");

    public MainMenuPane() {
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
        container.getChildren().add(title);
        container.getChildren().add(levelSelectButton);
        container.getChildren().add(levelEditorButton);
        container.getChildren().add(settingsButton);
        container.getChildren().add(quitButton);
        getChildren().add(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // Nothing to style here :)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        levelSelectButton.setOnAction(e->{
//            AudioManager.getInstance().playSound(AudioManager.SoundRes.WIN);
//            Label l = new Label();
//            Image IMAGE = new Image(ResourceLoader.getResource("assets/images/wall.png"));
//            l.setGraphic(new ImageView(IMAGE));
//            Scene scene = new Scene(l,50,50);
//            Stage stage = new Stage();
//            stage.setResizable(false);
//            stage.setScene(scene);
//            stage.show();
            SceneManager.getInstance().showPane(LevelSelectPane.class);
        });
        levelEditorButton.setOnAction(e->SceneManager.getInstance().showPane(LevelEditorPane.class));
        settingsButton.setOnAction(e->SceneManager.getInstance().showPane(SettingsPane.class));
        quitButton.setOnAction(e->Platform.exit());
    }
}
