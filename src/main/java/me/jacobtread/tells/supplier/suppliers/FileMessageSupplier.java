package me.jacobtread.tells.supplier.suppliers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessageSupplier extends StreamMessageSupplier {

    private final Path file;

    public FileMessageSupplier(Path file) {
        this(file, -1, false);
    }

    public FileMessageSupplier(Path file, int max) {
        this(file, max, false);
    }

    public FileMessageSupplier(Path file, boolean isRandom) {
        this(file, -1, isRandom);
    }

    /**
     * @param file     The file to read the messages from
     * @param max      The maximum amount of times it can be used
     * @param isRandom Whether or not to randomly return from the list
     */
    public FileMessageSupplier(Path file, int max, boolean isRandom) {
        super(null, max, isRandom);
        this.file = file;
    }

    @Override
    public void load() throws IOException {
        setStream(Files.newInputStream(file));
        super.load();
    }
}
