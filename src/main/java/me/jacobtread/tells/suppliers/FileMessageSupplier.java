package me.jacobtread.tells.suppliers;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class FileMessageSupplier implements Supplier<String> {

    private final Path file;
    private final int max;
    private final boolean isRandom;
    private final List<String> lines;
    private int count;
    private int index;
    private Random random;

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
     * @param file The file to read the messages from
     * @param max The maximum amount of messages to return
     * @param isRandom Whether or not to randomly return from the list
     */
    public FileMessageSupplier(Path file, int max, boolean isRandom) {
        this.file = file;
        this.max = max;
        this.isRandom = isRandom;
        if (isRandom) {
            random = new Random();
        }
        lines = new ArrayList<>();
    }

    public void load() throws IOException {
        BufferedReader bufferedReader = Files.newBufferedReader(file);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
    }

    public boolean isEmpty() {
        return lines.size() < 1;
    }

    @Override
    public String get() {
        if (count < max || max == -1) {
            int size = lines.size();
            String line;
            if (isRandom) {
                index = random.nextInt(lines.size() - 1);
                line = lines.get(index);
            } else {
                if (index < 0) {
                    index = 0;
                }
                line = lines.get(index);
                if (index + 1 < size) {
                    index++;
                } else {
                    index = 0;
                }
            }
            count++;
            return line;
        }
        return null;
    }

}
