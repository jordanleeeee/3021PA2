package io;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * A serializer for converting {@link GameProperties} into a map file.
 */
public class Serializer {

    /**
     * Path to the map to serialize to.
     */
    @NotNull
    private Path path;

    public Serializer(@NotNull final Path path) {
        this.path = path;
    }

    /**
     * Serializes a {@link GameProperties} object and saves it into a file.
     *
     * @param prop {@link GameProperties} object to serialize and save.
     * @throws IOException if an I/O exception has occurred.
     */
    public void serializeGameProp(@NotNull final GameProperties prop) throws IOException {
        // TODO
        File outputFile = new File(path.toString());    //will throw fileNotFound exception
        PrintWriter writer = new PrintWriter(outputFile);
        writer.println(prop.rows);
        writer.println(prop.cols);
        for (int i = 0; i < prop.rows; i++) {
            for (int j = 0; j < prop.cols; j++) {
                writer.print(prop.cells[i][j].toSingleChar());
            }
            writer.println();
        }
        writer.close();
    }
}
