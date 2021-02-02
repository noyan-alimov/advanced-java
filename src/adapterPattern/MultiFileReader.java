package adapterPattern;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MultiFileReader implements Closeable {

    private final List<BufferedReader> readers;

    public MultiFileReader(List<Path> paths) {
        // TODO: Build the List of BufferedReaders
        readers = new ArrayList<>(paths.size());
        try {
            for (Path path : paths) {
                readers.add(Files.newBufferedReader(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    public List<BufferedReader> getReaders() {
        return Collections.unmodifiableList(readers);
    }

    @Override
    public void close() {
        // TODO: Close all the readers.
        for (BufferedReader reader : readers) {
            try {
                reader.close();
            } catch (Exception ignored) {
            }
        }
    }
}
