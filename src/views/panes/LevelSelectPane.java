package views.panes;

import controllers.LevelManager;
import controllers.Renderer;
import controllers.SceneManager;
import io.Deserializer;
import io.GameProperties;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import models.FXGame;
import views.BigButton;
import views.BigVBox;
import views.SideMenuVBox;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

public class LevelSelectPane extends GamePane {

    private SideMenuVBox leftContainer = new SideMenuVBox();
    private BigButton returnButton = new BigButton("Return");
    private BigButton playButton = new BigButton("Play");
    private BigButton playRandom = new BigButton("Generate Map and Play");
    private BigButton chooseMapDirButton = new BigButton("Choose map directory");
    private ListView<String> levelsListView = new ListView<>(LevelManager.getInstance().getLevelNames());
    private BigVBox centerContainer = new BigVBox();
    private Canvas levelPreview = new Canvas();

    public LevelSelectPane() {
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
        leftContainer.getChildren().add(returnButton);
        leftContainer.getChildren().add(chooseMapDirButton);
        leftContainer.getChildren().add(levelsListView);
        leftContainer.getChildren().add(playButton);
        leftContainer.getChildren().add(playRandom);
        this.setLeft(leftContainer);
        centerContainer.getChildren().add(levelPreview);
        this.setCenter(centerContainer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
        playButton.setDisable(true);
        centerContainer.setAlignment(Pos.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        returnButton.setOnAction(e->SceneManager.getInstance().showPane(MainMenuPane.class));
        chooseMapDirButton.setOnAction((e)->promptUserForMapDirectory());
        //levelsListView.setOn
        playButton.setOnAction((e)->startGame(false));
        playRandom.setOnAction((e)->startGame(true));
        levelsListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String old_val, String new_val)
                        ->onMapSelected(ov, old_val, new_val));
    }

    /**
     * Starts the game.
     *
     * <p>
     * This method should do everything that is required to initialize and start the game, including loading/generating
     * maps, switching scenes, etc.
     * </p>
     *
     * @param generateRandom Whether to use a generated map.
     */
    private void startGame(final boolean generateRandom) {
        // TODO
        SceneManager.getInstance().showPane(GameplayPane.class);
        GameplayPane gameplayPane = SceneManager.getInstance().getPane(GameplayPane.class);
        if(generateRandom){
            gameplayPane.startGame(new FXGame());
        }
        else{
            try {
                Deserializer deserializer = new Deserializer(LevelManager.getInstance().getCurrentLevelPath().toString());
                FXGame fxGame = deserializer.parseFXGame();
                gameplayPane.startGame(fxGame);
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Listener method that executes when a map on the list is selected.
     *
     * @param observable Observable value.
     * @param oldValue   Original value.
     * @param newValue   New value.
     */
    private void onMapSelected(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // TODO
        System.out.println("hey.....");
        int index = levelsListView.getSelectionModel().getSelectedIndex();
        if(index == -1){
            playButton.setDisable(true);
            return;
        }
        try {
            LevelManager.getInstance().setLevel(levelsListView.getItems().get(index));
            System.out.println(LevelManager.getInstance().getCurrentLevelPath().toString());
            Deserializer deserializer = new Deserializer(LevelManager.getInstance().getCurrentLevelPath().toString());
            GameProperties gameProperties = deserializer.parseGameFile();
            Platform.runLater(() -> Renderer.renderMap(levelPreview, gameProperties.cells));
            playButton.setDisable(false);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Prompts the user for a map directory.
     *
     * <p>
     * Hint:
     * Use {@link DirectoryChooser} to display a folder selection prompt.
     * </p>
     */
    private void promptUserForMapDirectory() {
        // TODO
        DirectoryChooser d = new DirectoryChooser();
        File file = d.showDialog(new Stage());
        if(file != null){
            commitMapDirectoryChange(file);
        }
    }

    /**
     * Actually changes the current map directory.
     *
     * @param dir New directory to change to.
     */
    private void commitMapDirectoryChange(File dir) {
        // TODO
        LevelManager.getInstance().setMapDirectory(dir.toPath());
    }
}
