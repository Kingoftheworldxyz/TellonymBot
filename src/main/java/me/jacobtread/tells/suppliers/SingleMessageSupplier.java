package me.jacobtread.tells.suppliers;

import java.util.function.Supplier;

public class SingleMessageSupplier implements Supplier<String> {

    private final String message;
    private final int max;
    private int count;

    public SingleMessageSupplier(String message, int max) {
        this.message = message;
        this.max = max;
    }

    @Override
    public String get() {
        if (count < max) {
            count++;
            return message;
        }
        return null;
    }
}
