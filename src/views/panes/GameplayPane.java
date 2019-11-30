package views.panes;

import controllers.AudioManager;
import controllers.LevelManager;
import controllers.SceneManager;
import io.Deserializer;
import io.Serializer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.FXGame;
import org.jetbrains.annotations.NotNull;
import views.BigButton;
import views.BigVBox;
import views.GameplayInfoPane;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import static models.Config.TILE_SIZE;
//import facebook4j.Facebook;

/**
 * Pane for displaying the actual gameplay.
 */
public class GameplayPane extends GamePane{

    private HBox topBar = new HBox(20);
    private VBox canvasContainer = new BigVBox();
    private Canvas gameplayCanvas = new Canvas();
    private HBox bottomBar = new HBox(20);
    private Canvas queueCanvas = new Canvas();
    private Button quitToMenuButton = new BigButton("Quit to menu");
    private Button pauseButton = new BigButton("Pause");

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
        infoPane = new GameplayInfoPane(new SimpleStringProperty(), ticksElapsed, new SimpleIntegerProperty(), new SimpleIntegerProperty(), -1);
        topBar.getChildren().add(infoPane);
        this.setTop(topBar);
        topBar.setAlignment(Pos.CENTER);
        canvasContainer.getChildren().add(gameplayCanvas);
        canvasContainer.setAlignment(Pos.CENTER);
        this.setCenter(canvasContainer);
        bottomBar.getChildren().add(queueCanvas);
        bottomBar.getChildren().add(quitToMenuButton);
        bottomBar.getChildren().add(pauseButton);
        this.setBottom(bottomBar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
        gameplayCanvas.setStyle("-fx-background-color: rgb(50, 168, 50);");
        queueCanvas.setStyle("-fx-background-color: #ddd;");
        bottomBar.setStyle("-fx-padding: 20 20 20 20; -fx-background-color: #ccc;");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        gameplayCanvas.setOnMouseClicked(e->onCanvasClicked(e));
        quitToMenuButton.setOnAction(e->doQuitToMenuAction());
        setOnKeyPressed(e->onKeyPressed(e));

        pauseButton.setOnAction(event-> {
            game.pauseAndPlay();
            if(pauseButton.getText() == "Pause"){
                pauseButton.setText("Play");
                gameplayCanvas.setOnMouseClicked(null);
                setOnKeyPressed(null);
            }
            else{
                pauseButton.setText("Pause");
                gameplayCanvas.setOnMouseClicked(e->onCanvasClicked(e));
                setOnKeyPressed(e->onKeyPressed(e));
            }
        });
    }

    /**
     * Handles events when somewhere on the {@link GameplayPane#gameplayCanvas} is clicked.
     *
     * @param event Event to handle.
     */
    private void onCanvasClicked(MouseEvent event) {
        // TODO
        int col = (int)event.getX()/TILE_SIZE;
        int row = (int)event.getY()/TILE_SIZE;
        game.placePipe(row,col);
        game.renderQueue(queueCanvas);
        game.renderMap(gameplayCanvas);
        updateInfoPane();

        //check if winning after placing a pipe
        if(game.hasWon()){
            createWinPopup();
        }
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
            game.renderQueue(queueCanvas);
            game.renderMap(gameplayCanvas);
            updateInfoPane();
        }
        if(event.getCode()== KeyCode.S){
            //skip pipe
            game.skipPipe();
            game.renderQueue(queueCanvas);
            updateInfoPane();
        }
    }

    /**
     * Creates a popup which tells the player they have completed the map.
     */
    private void createWinPopup() {
        // TODO
        if(game.isRecordBeaked()){
            recordBeakedReaction();
        }
        ButtonType back = new ButtonType("Return");
        ButtonType next = new ButtonType("Next Map");
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, null, next, back);
        a.setHeaderText("Level Cleared!");
        a.showAndWait().ifPresent(type -> {
            if (type == back) {
                SceneManager.getInstance().showPane(LevelSelectPane.class);
            }
            else{
                loadNextMap();
            }
        });
    }

    /**
     *  get the map file of the current game, change the value of best record in the file
     *  by calling void serializeGameProp(Arraylist<String> paragraph)
     */
    private void recordBeakedReaction(){
        File file = new File(LevelManager.getInstance().getCurrentLevelPath().toString());
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            ArrayList<String> list = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            //System.out.println(list.toString());
            for(int i=0; i<list.size(); i++){
                if(list.get(i).contains("Best Record: ")){
                    list.set(i, "Best Record: "+ game.getBestRecord());
                }
            }
            //System.out.println(list.toString());
            Serializer serializer = new Serializer(file.toPath());
            serializer.serializeGameProp(list);
            fr.close();
            br.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Loads the next map in the series, or generate a new map if one is not available.
     */
    private void loadNextMap() {
        // TODO
        String game = LevelManager.getInstance().getAndSetNextLevel();
        if(game==null){
            startGame(new FXGame());
        }
        else{
            try {
                Deserializer deserializer = new Deserializer(LevelManager.getInstance().getCurrentLevelPath().toString());
                startGame(deserializer.parseFXGame());
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        ticksElapsed.setValue(0);
    }

    /**
     * Creates a popup which tells the player they have lost the map.
     */
    private void createLosePopup() {
        // TODO
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,null, new ButtonType("Return"));
        a.setHeaderText("You lose!");
        a.showAndWait();
        doQuitToMenu();
    }

    /**
     * Creates a popup which prompts the player whether they want to quit.
     */
    private void doQuitToMenuAction() {
        // TODO
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,"return to menu?", ButtonType.OK, ButtonType.CANCEL);
        a.setContentText("game status will lose...");
        a.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                doQuitToMenu();
            }
        });
    }

    /**
     * Go back to the Level Select scene.
     */
    private void doQuitToMenu() {
        // TODO
        game.stopCountdown();
        ticksElapsed.setValue(0);
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
        updateInfoPane();

        //System.out.println("game start");
        if(FXGame.isDefaultCountDownEnabled()){
            ticksElapsed.setValue(100);
        }
        game.addOnFlowHandler(()->{         //each flow happened
            //System.out.println("flow");
            game.updateState();
            game.renderMap(gameplayCanvas);
            if(game.hasLost()){
                game.stopCountdown();
                endGame();
            }
        });
        game.addOnTickHandler(()->{         //each second past
            ticksElapsed.setValue(FXGame.isDefaultCountDownEnabled()?ticksElapsed.intValue()-1:ticksElapsed.intValue()+1);
            updateInfoPane();
            if(FXGame.isDefaultCountDownEnabled()){
                if(ticksElapsed.get()<=0){
                    game.stopCountdown();
                    AudioManager.getInstance().playSound(AudioManager.SoundRes.LOSE);
                    endGame();
                }//lose
            }
        });
        game.renderQueue(queueCanvas);
        game.renderMap(gameplayCanvas);
        game.startCountdown();
    }

    /**
     * Cleans up the currently bound game.
     */
    private void endGame() {
        // TODO
        Platform.runLater(()->createLosePopup());
    }

    private void updateInfoPane(){
        StringProperty levelName;
        if(LevelManager.getInstance().getCurrentLevelProperty().get() != null) {
            levelName = new SimpleStringProperty(LevelManager.getInstance().getCurrentLevelPath().getFileName().toString());
        }
        else{
            levelName = new SimpleStringProperty("<Generate>");
        }
        Platform.runLater(()->topBar.getChildren().set(0, new GameplayInfoPane(levelName, ticksElapsed, game.getNumOfSteps(), game.getNumOfUndo(), game.getBestRecord())));
    }
}
