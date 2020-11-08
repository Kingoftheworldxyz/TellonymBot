package me.jacobtread.tells.supplier.suppliers;

import me.jacobtread.tells.supplier.MessageSupplier;

public class SingleMessageSupplier implements MessageSupplier {

    private final String message;
    private final int max;
    private int count;

    public SingleMessageSupplier(String message) {
        this(message, -1);
    }

    /**
     * @param message The message to provide
     * @param max     The maximum amount of times it can be used
     */
    public SingleMessageSupplier(String message, int max) {
        this.message = message;
        this.max = max;
    }

    @Override
    public boolean hasMore() {
        return count < max;
    }

    @Override
    public String next() {
        count++;
        return message;
    }
}
