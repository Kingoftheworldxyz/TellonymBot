package me.jacobtread.tells.supplier;

public interface MessageSupplier {

    /**
     * @return Whether or not their are more messages to send
     */
    boolean hasMore();

    /**
     * Provides the next message
     *
     * @return The next message to be sent
     */
    String next();

}
