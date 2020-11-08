package me.jacobtread.tells.supplier.suppliers;

import me.jacobtread.tells.supplier.MessageSupplier;

import java.util.List;
import java.util.Random;

public class ListMessageSupplier implements MessageSupplier {

    private final int max;
    private final boolean isRandom;
    private final List<String> messages;
    private int count;
    private int index;
    private Random random;

    public ListMessageSupplier(List<String> messages) {
        this(messages, -1, false);
    }

    public ListMessageSupplier(List<String> messages, int max) {
        this(messages, max, false);
    }

    public ListMessageSupplier(List<String> messages, boolean isRandom) {
        this(messages, -1, isRandom);
    }

    /**
     * @param messages The list of messages to use
     * @param max      The maximum amount of messages to return
     * @param isRandom Whether or not to randomly return from the list
     */
    public ListMessageSupplier(List<String> messages, int max, boolean isRandom) {
        this.messages = messages;
        this.max = max;
        this.isRandom = isRandom;
        if (isRandom) {
            random = new Random();
        }
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    @Override
    public boolean hasMore() {
        return max == -1 || count < max;
    }

    @Override
    public String next() {
        int size = messages.size();
        String line;
        if (isRandom) {
            index = random.nextInt(messages.size() - 1);
            line = messages.get(index);
        } else {
            if (index < 0) {
                index = 0;
            }
            line = messages.get(index);
            if (index + 1 < size) {
                index++;
            } else {
                index = 0;
            }
        }
        count++;
        return line;
    }



}
