package controllers;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import views.panes.*;

import java.time.Instant;
import java.util.Map;

/**
 * Singleton class for managing scenes.
 */
public class SceneManager {

    /**
     * Singleton instance.
     */
    private static final SceneManager INSTANCE = new SceneManager();

    /**
     * Main menu scene.
     */
    @NotNull
    private final Scene mainMenuScene = new Scene(new MainMenuPane(), Config.WIDTH / 2.0, Config.HEIGHT);
    /**
     * Settings scene.
     */
    @NotNull
    private final Scene settingsScene = new Scene(new SettingsPane(), Config.WIDTH, Config.HEIGHT);
    /**
     * Level select scene.
     */
    @NotNull
    private final Scene levelSelectScene = new Scene(new LevelSelectPane(), Config.WIDTH, Config.HEIGHT);
    /**
     * Gameplay scene.
     */
    @NotNull
    private final Scene gameplayScene = new Scene(new GameplayPane(), Config.WIDTH, Config.HEIGHT);
    /**
     * Level editor scene.
     */
    @NotNull
    private final Scene levelEditorScene = new Scene(new LevelEditorPane(), Config.WIDTH, Config.HEIGHT);
    /**
     * Map for fast lookup of {@link GamePane} to their respective {@link Scene}.
     */
    @NotNull
    private final Map<Class<? extends GamePane>, Scene> scenes = Map.ofEntries(
            Map.entry(MainMenuPane.class, mainMenuScene),
            Map.entry(SettingsPane.class, settingsScene),
            Map.entry(LevelSelectPane.class, levelSelectScene),
            Map.entry(GameplayPane.class, gameplayScene),
            Map.entry(LevelEditorPane.class, levelEditorScene)
    );
    /**
     * Primary stage.
     */
    @Nullable
    private Stage stage;

    private SceneManager() {

        // TODO: Add CSS styles to every scene
        mainMenuScene.getStylesheets().add("file:resources/assets/styles/styles.css");
        settingsScene.getStylesheets().add("file:resources/assets/styles/styles.css");
        levelEditorScene.getStylesheets().add("file:resources/assets/styles/styles.css");
        levelSelectScene.getStylesheets().add("file:resources/assets/styles/styles.css");
        gameplayScene.getStylesheets().add("file:resources/assets/styles/styles.css");
    }

    /**
     * Sets the primary stage.
     *
     * @param stage Primary stage.
     */
    public void setStage(@NotNull final Stage stage) {
        if (this.stage != null) {
            throw new IllegalStateException("Primary stage is already initialized!");
        }

        this.stage = stage;
    }

    /**
     * Replaces the currently active {@link Scene} with another one.
     *
     * @param scene New scene to display.
     */
    private void showScene(@NotNull final Scene scene) {
        if (stage == null) {
            return;
        }

        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Replaces the current {@link GamePane} with another one.
     *
     * @param pane New pane to display.
     * @throws IllegalArgumentException If the {@code pane} is not known.
     */
    public void showPane(@NotNull final Class<? extends GamePane> pane) {
        //TODO
        if(pane == GameplayPane.class){
            stage.setScene(gameplayScene);
            showScene(gameplayScene);
        }
        else if(pane == LevelEditorPane.class){
            stage.setScene(levelEditorScene);
            showScene(levelEditorScene);
        }
        else if(pane == LevelSelectPane.class){
            stage.setScene(levelSelectScene);
            showScene(levelSelectScene);
        }
        else if(pane == MainMenuPane.class){
            stage.setScene(mainMenuScene);
            showScene(mainMenuScene);
        }
        else if(pane == SettingsPane.class){
            stage.setScene(settingsScene);
            showScene(settingsScene);
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Retrieves the underlying singleton {@link GamePane} object.
     *
     * @param pane {@link Class} type of pane to retrieve.
     * @param <T>  Actual type of the {@link GamePane} object.
     * @return Handle to the singleton {@link GamePane} object.
     */
    public <T> T getPane(@NotNull final Class<? extends GamePane> pane) {
        //noinspection unchecked
        return (T) scenes.get(pane).getRoot();
    }

    @NotNull
    public static SceneManager getInstance() {
        return INSTANCE;
    }
}
