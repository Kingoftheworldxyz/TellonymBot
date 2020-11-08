package me.jacobtread.tells.supplier.suppliers;

import me.jacobtread.tells.supplier.IOSupplier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StreamMessageSupplier extends ListMessageSupplier implements IOSupplier {

    private InputStream stream;

    public StreamMessageSupplier(InputStream stream) {
        this(stream, -1, false);
    }

    public StreamMessageSupplier(InputStream stream, int max) {
        this(stream, max, false);
    }

    public StreamMessageSupplier(InputStream stream, boolean isRandom) {
        this(stream, -1, isRandom);
    }

    /**
     * @param stream The stream to read the messages from
     * @param max The maximum amount of times it can be used
     * @param isRandom Whether or not to randomly return from the list
     */
    public StreamMessageSupplier(InputStream stream, int max, boolean isRandom) {
        super(new ArrayList<>(), max, isRandom);
        this.stream = stream;
    }

    /**
     * Loads the messages from the source
     * 
     * @throws IOException An exception when trying to read from the stream
     */
    @Override
    public void load() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addMessage(line);
            }
        }
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }
}
