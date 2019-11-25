package views;

import controllers.Renderer;
import io.Deserializer;
import io.GameProperties;
import io.Serializer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.exceptions.InvalidMapException;
import models.map.cells.Cell;
import models.map.cells.FillableCell;
import models.map.cells.TerminationCell;
import models.map.cells.Wall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static models.Config.TILE_SIZE;

public class LevelEditorCanvas extends Canvas {

    private static final String MSG_MISSING_SOURCE = "Source tile is missing!";
    private static final String MSG_MISSING_SINK = "Sink tile is missing!";
    private static final String MSG_BAD_DIMS = "Map size must be at least 2x2!";
    private static final String MSG_BAD_DELAY = "Delay must be a positive value!";
    private static final String MSG_SOURCE_TO_WALL = "Source tile is blocked by a wall!";
    private static final String MSG_SINK_TO_WALL = "Sink tile is blocked by a wall!";

    private GameProperties gameProp;

    @Nullable
    private TerminationCell sourceCell;
    @Nullable
    private TerminationCell sinkCell;

    public LevelEditorCanvas(int rows, int cols, int delay) {
        super();

        resetMap(rows, cols, delay);
    }

    /**
     * Changes the attributes of this canvas.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    public void changeAttributes(int rows, int cols, int delay) {
        resetMap(rows, cols, delay);
    }

    /**
     * Resets the map with the given attributes.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    private void resetMap(int rows, int cols, int delay) {
        // TODO
        sinkCell = null;
        sourceCell = null;
        Cell cells[][] = new Cell[rows][cols];
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(i==0 || i==rows-1 || j==0 || j==cols-1){
                    cells[i][j] = new Wall(new Coordinate(i, j));
                }
                else{
                    cells[i][j] = new FillableCell(new Coordinate(i, j));
                }
            }
        }
        gameProp = new GameProperties(rows, cols, cells, delay);
        renderCanvas();
    }

    /**
     * Renders the canvas.
     */
    private void renderCanvas() {
        Platform.runLater(() -> Renderer.renderMap(this, gameProp.cells));
    }

    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You may need to check/compute some attribute in order to create the new {@link Cell} object.
     *
     * @param sel Selected {@link CellSelection}.
     * @param x   X-coordinate relative to the canvas.
     * @param y   Y-coordinate relative to the canvas.
     */
    public void setTile(@NotNull CellSelection sel, double x, double y) {
        // TODO
        int col = (int)x/TILE_SIZE;  //col
        int row = (int)y/TILE_SIZE;  //row
        System.out.println("x= "+row+" y= "+col);
        if(sel == CellSelection.WALL || sel == CellSelection.CELL){
            Cell target = gameProp.cells[row][col];
            if(target instanceof TerminationCell){
                if(((TerminationCell) target).type == TerminationCell.Type.SOURCE){
                    sourceCell = null;
                }
                else{
                    sinkCell = null;
                }
            }
            if(sel == CellSelection.WALL){
                setTileByMapCoord(new Wall(new Coordinate(row,col)));
            }
            else{
                setTileByMapCoord(new FillableCell(new Coordinate(row,col)));
            }
        }
        else {
            if ((row == 0 && col == 0) || (row == 0 && col == gameProp.cols - 1) || (col == 0 && row == gameProp.rows - 1)
                    || (col == gameProp.cols - 1 && row == gameProp.rows - 1)) {
                return;
            }
            if (row == 0 || col == 0 || row == gameProp.rows - 1 || col == gameProp.cols - 1){
                if(sinkCell!=null){
                    return;
                }
                if (row == 0) {       //if it is a sink top
                    sinkCell = new TerminationCell(new Coordinate(row, col), Direction.UP, TerminationCell.Type.SINK);
                    setTileByMapCoord(sinkCell);
                } else if (col == 0) {       //if it is a left
                    System.out.println("hi");
                    sinkCell = new TerminationCell(new Coordinate(row, col), Direction.LEFT, TerminationCell.Type.SINK);
                    setTileByMapCoord(sinkCell);
                } else if (row == gameProp.rows - 1) {       //if it is a sink bottom
                    sinkCell = new TerminationCell(new Coordinate(row, col), Direction.DOWN, TerminationCell.Type.SINK);
                    setTileByMapCoord(sinkCell);
                } else {       //if it is a sink right
                    sinkCell = new TerminationCell(new Coordinate(row, col), Direction.RIGHT, TerminationCell.Type.SINK);
                    setTileByMapCoord(sinkCell);
                }
            }
            else{   //it is a source
                if(sourceCell!=null){
                    return;
                }
                sourceCell = new TerminationCell(new Coordinate(row,col), Direction.UP, TerminationCell.Type.SOURCE);
                setTileByMapCoord(sourceCell);
            }
        }
        renderCanvas();
    }

    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You will need to make sure that there is only one source/sink cells in the map.
     *
     * @param cell The {@link Cell} object to set.
     */
    private void setTileByMapCoord(@NotNull Cell cell) {
        // TODO
        gameProp.cells[cell.coord.row][cell.coord.col] = cell;
    }

    /**
     * Toggles the rotation of the source tile clockwise.
     */
    public void toggleSourceTileRotation() {
        // TODO
        if(sourceCell != null) {
            sourceCell = new TerminationCell(sourceCell.coord, sourceCell.pointingTo.rotateCW(), TerminationCell.Type.SOURCE);
            setTileByMapCoord(sourceCell);
            renderCanvas();
        }
    }

    /**
     * Loads a map from a file.
     * <p>
     * Prompts the player if they want to discard the changes, displays the file chooser prompt, and loads the file.
     *
     * @return {@code true} if the file is loaded successfully.
     */
    public boolean loadFromFile() {
        // TODO
        Alert info = new Alert(Alert.AlertType.INFORMATION, "current map contents will be lost.", ButtonType.OK, ButtonType.CANCEL);
        info.setHeaderText("Load a map form file?");
        Optional<ButtonType> buttonClicked = info.showAndWait();
        if (buttonClicked.isPresent()) {
            if(buttonClicked.get() == ButtonType.OK);
            File f = getTargetLoadFile();
            return loadFromFile(f.toPath());
        }
        return false;
    }

    /**
     * Prompts the user for the file to load.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to load, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetLoadFile() {
        // TODO
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Map File", "*.map"));
        return fileChooser.showOpenDialog(new Stage());
    }

    /**
     * Loads the file from the given path and replaces the current {@link LevelEditorCanvas#gameProp}.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from loading in this method.
     *
     * @param path Path to load the file from.
     * @return {@code true} if the file is loaded successfully, {@code false} otherwise.
     */
    private boolean loadFromFile(@NotNull Path path) {
        // TODO
        try{
            Deserializer deserializer = new Deserializer(path);
            this.gameProp = deserializer.parseGameFile();
            for(int i=0; i<gameProp.cells.length; i++){
                for(int j=0; j<gameProp.cells[0].length; j++){
                    if(i==0||i==gameProp.cells.length-1||j==0||j==gameProp.cells.length-1){
                        if(gameProp.cells[i][j] instanceof TerminationCell){
                            sinkCell = (TerminationCell)gameProp.cells[i][j];
                            //System.out.println("sink to "+sinkCell.pointingTo);
                        }
                    }
                    else{
                        if(gameProp.cells[i][j] instanceof TerminationCell){
                            sourceCell = (TerminationCell)gameProp.cells[i][j];
                            //System.out.println("source to "+sourceCell.pointingTo);
                        }
                    }
                }
            }
            renderCanvas();
            return true;
        }
        catch (FileNotFoundException e){
            return false;
        }
    }

    /**
     * Checks the validity of the map, prompts the player for the target save directory, and saves the file.
     */
    public void saveToFile() {
        // TODO
        if(checkValidity().isEmpty()){
            File target = getTargetSaveDirectory();
            if(target != null) {
                exportToFile(target.toPath());
            }
        }
        else{
            String warning = checkValidity().get();
            Alert error = new Alert(Alert.AlertType.ERROR, warning, ButtonType.OK);
            error.setHeaderText("Map validity check failed");
            error.show();
        }
    }

    /**
     * Prompts the user for the directory and filename to save as.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to save to, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetSaveDirectory() {
        // TODO
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter mapExtensionFilter = new FileChooser.ExtensionFilter("Map format", "*.map");
        fileChooser.getExtensionFilters().add(mapExtensionFilter);
        fileChooser.setSelectedExtensionFilter(mapExtensionFilter);
        return fileChooser.showSaveDialog(new Stage());
    }

    /**
     * Exports the current map to a file.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from saving in this method.
     *
     * @param p Path to export to.
     */
    private void exportToFile(@NotNull Path p) {
        // TODO
        Serializer serializer = new Serializer(p);
        try{
            serializer.serializeGameProp(gameProp);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the current map and its properties are valid.
     * <p>
     * Hint:
     * You should check for the following conditions:
     * <ul>
     * <li>Source cell is present</li>
     * <li>Sink cell is present</li>
     * <li>Minimum map size is 2x2</li>
     * <li>Flow delay is at least 1</li>
     * <li>Source/Sink tiles are not blocked by walls</li>
     * </ul>
     *
     * @return {@link Optional} containing the error message, or an empty {@link Optional} if the map is valid.
     */
    private Optional<String> checkValidity() {
        // TODO
        if(sourceCell == null){
           return Optional.of(MSG_MISSING_SOURCE);
        }
        else if(sinkCell == null){
            return Optional.of(MSG_MISSING_SINK);
        }
        else if(getNumOfCols()<2 || getNumOfRows()<2){
            return Optional.of(MSG_BAD_DIMS);
        }
        else if(getAmountOfDelay()<1){
            return Optional.of(MSG_BAD_DELAY);
        }
        Coordinate sinkCellFacing = sinkCell.coord.add(sinkCell.pointingTo.getOpposite().getOffset());
        if(gameProp.cells[sinkCellFacing.row][sinkCellFacing.col] instanceof Wall){      //check if sink is blocked
            return Optional.of(MSG_SINK_TO_WALL);
        }
        Coordinate sourceCellFacing = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if(gameProp.cells[sourceCellFacing.row][sourceCellFacing.col] instanceof Wall){     //check if source is blocked
            return Optional.of(MSG_SOURCE_TO_WALL);
        }

        return Optional.empty();

    }

    public int getNumOfRows() {
        return gameProp.rows;
    }

    public int getNumOfCols() {
        return gameProp.cols;
    }

    public int getAmountOfDelay() {
        return gameProp.delay;
    }

    public void setAmountOfDelay(int delay) {
        gameProp.delay = delay;
    }

    public enum CellSelection {
        WALL("Wall"),
        CELL("Cell"),
        TERMINATION_CELL("Source/Sink");

        private String text;

        CellSelection(@NotNull String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
