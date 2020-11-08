package me.jacobtread.tells.supplier;

import java.io.IOException;

public interface IOSupplier extends MessageSupplier {

    /**
     * Load any data that could cause an IOException
     *
     * @throws IOException An exception caused when loading
     */
    void load() throws IOException;

}
