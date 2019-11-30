package models;

import controllers.AudioManager;
import controllers.ResourceLoader;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.map.Map;
import models.map.cells.Cell;
import models.map.cells.FillableCell;
import models.pipes.Pipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import textgame.game.Game;
import util.Coordinate;

import java.util.List;

/**
 * JavaFX version of {@link textgame.game.Game}.
 */
public class FXGame {

    /**
     * Default number of rows.
     */
    private static int defaultRows = 8;
    /**
     * Default number of columns.
     */
    private static int defaultCols = 8;
    /**
     * Default number of columns.
     */
    private static boolean defaultCountDownEnabled = false;

    @NotNull
    private final Map map;
    @NotNull
    private final PipeQueue pipeQueue;
    @NotNull
    private final FlowTimer flowTimer;
    @NotNull
    private final CellStack cellStack = new CellStack();

    private IntegerProperty numOfSteps = new SimpleIntegerProperty(0);

    private boolean isPlaying = true;
    private boolean goldFingerUsed = false;
    @Nullable
    private Integer bestRecord = null;          //if the game do not take any record it is null
    private boolean recordBeaked = false;

    /**
     * Sets the default number of rows for generated maps.
     *
     * @param rows New default number of rows.
     */
    public static void setDefaultRows(int rows) {
        defaultRows = rows;
    }

    /**
     * Sets the default number of column for generated maps.
     *
     * @param cols New default number of columns.
     */
    public static void setDefaultCols(int cols) {
        defaultCols = cols;
    }

    public static void setDefaultCountDownEnabled(boolean enabled) { defaultCountDownEnabled = enabled; }
    /**
     * @return Current default number of rows for generated maps.
     */
    public static int getDefaultRows() {
        return defaultRows;
    }

    /**
     * @return Current default number of columns for generated maps.
     */
    public static int getDefaultCols() {
        return defaultCols;
    }

    public static boolean isDefaultCountDownEnabled() {
        return defaultCountDownEnabled;
    }

    /**
     * Constructs an instance with default number of rows and columns.
     */
    public FXGame() {
        this(defaultRows, defaultCols);
    }

    /**
     * Constructs an instance with given number of rows and columns.
     *
     * @param rows Number of rows (excluding side walls)
     * @param cols Number of columns (excluding side walls)
     */
    private FXGame(int rows, int cols) {
        // TODO
        map = new Map(rows+2, cols+2);
        pipeQueue = new PipeQueue();
        flowTimer = new FlowTimer();
    }

    /**
     * Constructs an instance with all given parameters.
     *
     * @param rows  Number of rows including side walls
     * @param cols  Number of columns including side walls
     * @param delay Delay in seconds before water starts flowing.
     * @param cells Initial map.
     * @param pipes Initial pipes, if provided.
     */
    public FXGame(int rows, int cols, int delay, @NotNull Cell[][] cells, @Nullable List<Pipe> pipes) {
        // TODO
        map = new Map(rows, cols, cells);
        pipeQueue = new PipeQueue(pipes);
        flowTimer = new FlowTimer(delay);
    }

    //constructor for initialize value of bestRecord
    public FXGame(int rows, int cols, int delay, @NotNull Cell[][] cells, @Nullable List<Pipe> pipes, int bestRecord) {
        // TODO
        this(rows, cols, delay, cells, pipes);
        this.bestRecord = bestRecord;
    }

    /**
     * Adds a handler to be run when the water flows into an additional tile.
     *
     * @param handler {@link Runnable} to execute.
     */
    public void addOnFlowHandler(@NotNull Runnable handler) {
        flowTimer.registerFlowCallback(handler);
    }

    /**
     * Adds a handler to be run when a tick elapses.
     *
     * @param handler {@link Runnable} to execute.
     */
    public void addOnTickHandler(@NotNull Runnable handler) {
        flowTimer.registerTickCallback(handler);
    }

    /**
     * Starts the flow of water.
     */
    public void startCountdown() {
        flowTimer.start();
    }

    /**
     * Stops the flow of water.
     */
    public void stopCountdown() {
        flowTimer.stop();
    }

    /**
     * @param row Row index to place pipe
     * @param col Column index to place pipe
     * @see Game#placePipe(int, char)
     */
    public void placePipe(int row, int col) {
        // TODO
        if(!map.tryPlacePipe(new Coordinate(row, col), pipeQueue.peek())){
            if(map.isGoldFingerActivated() && !goldFingerUsed){
                goldFingerOperation(row, col);
            }
            return;
        }
        //play a sound if place pipe is successful
        AudioManager.getInstance().playSound(AudioManager.SoundRes.MOVE);

        cellStack.push(new FillableCell(new Coordinate(row, col), pipeQueue.peek()));
        pipeQueue.consume();
        numOfSteps.set(numOfSteps.intValue()+1);
    }

    /**
     * @see Game#skipPipe()
     */
    public void skipPipe() {
        // TODO
        numOfSteps.set(numOfSteps.intValue()+1);
        //System.out.println(numOfSteps.intValue());
        pipeQueue.consume();
    }

    /**
     * @see Game#undoStep()
     */
    public void undoStep() {
        // TODO
        FillableCell fillableCell = cellStack.pop();
        if(fillableCell == null){
            return;
        }
        if(fillableCell.getPipe().get().getFilled()){
            cellStack.push(fillableCell);
            return;
        }
        else{
            pipeQueue.undo(fillableCell.getPipe().get());
            map.undo(fillableCell.coord);
            getNumOfSteps().setValue(getNumOfSteps().intValue()+1);
        }
    }

    /**
     * Renders the map onto a {@link Canvas}.
     *
     * @param canvas {@link Canvas} to render to.
     */
    public void renderMap(@NotNull Canvas canvas) {
        map.render(canvas);
    }

    /**
     * Renders the queue onto a {@link Canvas}.
     *
     * @param canvas {@link Canvas} to render to.
     */
    public void renderQueue(@NotNull Canvas canvas) {
        pipeQueue.render(canvas);
    }

    /**
     * @see Game#updateState()
     */
    public void updateState() {
        // TODO
        int distance = flowTimer.distance();
        if(distance<0){
            return;
        }
        if(distance==0){
            map.fillBeginTile();
            return;
        }
        map.fillTiles(distance);
    }

    /**
     * @see Game#updateState()
     */
    public boolean hasWon() {
        // TODO
        if(map.checkPath()){
            AudioManager.getInstance().playSound(AudioManager.SoundRes.WIN);
            stopCountdown();
            fillAllPipes();
            if(bestRecord != null){
                if(flowTimer.getRealTicksElapsed()<bestRecord){
                    Alert breakRecord = new Alert(Alert.AlertType.INFORMATION, "you break the record", ButtonType.OK);
                    recordBeaked = true;
                    bestRecord = flowTimer.getRealTicksElapsed();
                    breakRecord.showAndWait();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @see Game#hasLost()
     */
    public boolean hasLost() {
        // TODO
        if(map.hasLost() && flowTimer.distance() > 0){
            AudioManager.getInstance().playSound(AudioManager.SoundRes.LOSE);
            return true;
        }
        return false;
    }

    /**
     * do operation on the timer
     * update the value of isPlaying
     */
    public void pauseAndPlay(){
        if(isPlaying){
            flowTimer.pauseAndResume(true);
            isPlaying = false;
        }
        else{
            flowTimer.pauseAndResume(false);
            isPlaying = true;
        }
    }

    /**
     * show new stage with 7 button with un-filled pipe image and wait until the stage is closed,
     * set on action on each button, when a user click any button the pipe will be replaced (
     * by calling forcePlacePipe() method in Map class and the new stage will be closed.
     * Finally set goldFingerUsed be true so that gold finger can use once only
     * @param row   row of the pipe that will be replaced
     * @param col   col of the pipe that will be replaced
     */
    private void goldFingerOperation(int row, int col){
        pauseAndPlay();
        Stage stage = new Stage();
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        String imageList[] = {"horizontal-unfilled", "vert-unfilled", "top-left-unfilled",
                "top-right-unfilled", "bottom-left-unfilled", "bottom-right-unfilled", "cross-unfilled"};
        for(int i=0; i<imageList.length; i++){
            final Image CORNER_UNFILLED = new Image(ResourceLoader.getResource("assets/images/"+ imageList[i]+".png"));
            Button x = new Button();
            final int finalI = i;
            x.setOnAction(e -> {
                map.forcePlacePipe(row, col, new Pipe(Pipe.Shape.values()[finalI]));
                stage.close();
                pauseAndPlay();
            });
            x.setGraphic(new ImageView(CORNER_UNFILLED));
            buttons.getChildren().add(x);
        }
        Scene scene = new Scene(buttons, Config.TILE_SIZE*12, Config.TILE_SIZE+10);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.setTitle("Pipes Replace Options");
        stage.showAndWait();
        goldFingerUsed = true;
    }

    /**
     * Fills all reachable pipes in the map.
     */
    public void fillAllPipes() {
        map.fillAll();
    }

    public IntegerProperty getNumOfSteps() {
        return numOfSteps;
    }

    public IntegerProperty getNumOfUndo() {
        return cellStack.getUndoCountProperty();
    }

    public Integer getBestRecord() {
        return bestRecord;
    }

    public boolean isRecordBeaked() {
        return recordBeaked;
    }
}
