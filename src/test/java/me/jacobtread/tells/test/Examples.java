package me.jacobtread.tells.test;

import me.jacobtread.tells.TellonymBot;
import me.jacobtread.tells.supplier.MessageSupplier;
import me.jacobtread.tells.supplier.suppliers.FileMessageSupplier;
import me.jacobtread.tells.supplier.suppliers.ListMessageSupplier;
import me.jacobtread.tells.supplier.suppliers.SingleMessageSupplier;
import me.jacobtread.tells.supplier.suppliers.StreamMessageSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Examples {

    public static void streamExample() {
        String account = "example";
        int max = 5;
        InputStream resourceStream = Examples.class.getResourceAsStream("/messages.txt");
        StreamMessageSupplier messageSupplier = new StreamMessageSupplier(resourceStream, max);
        try {
            messageSupplier.load();
            TellonymBot bot = new TellonymBot(account, messageSupplier);
            bot.run();
        } catch (IOException e) {
            System.err.println("Unable to load messages from stream");
            e.printStackTrace();
        }
    }

    public static void fileExample() {
        String account = "example";
        Path file = Paths.get("path/to/your/file");
        int max = 5;
        FileMessageSupplier messageSupplier = new FileMessageSupplier(file, max);
        try {
            messageSupplier.load();
            TellonymBot bot = new TellonymBot(account, messageSupplier);
            bot.run();
        } catch (IOException e) {
            System.err.println("Unable to load messages from file");
            e.printStackTrace();
        }
    }

    public static void textExample() {
        String account = "jacobtread";
        String message = "This is an example tell.";
        int max = 5;
        SingleMessageSupplier messageSupplier = new SingleMessageSupplier(message, max);
        TellonymBot bot = new TellonymBot(account, messageSupplier);
        bot.run();
    }

    public static void listExample() {
        String account = "example";
        List<String> examples = Arrays.asList("This is an example tell.", "Hey how are you doing?", "Bingo Bongo this is a Bot.");
        int max = 5;
        ListMessageSupplier messageSupplier = new ListMessageSupplier(examples, max);
        TellonymBot bot = new TellonymBot(account, messageSupplier);
        bot.run();
    }

    public static void customExample() {
        String account = "example";
        MessageSupplier messageSupplier = new MessageSupplier() {
            @Override
            public boolean hasMore() {
                return true; // TODO: Return false to indicate no more messages
            }

            @Override
            public String next() {
                return ""; // TODO: Return the message to send
            }
        };
        TellonymBot bot = new TellonymBot(account, messageSupplier);
        bot.run();
    }

}
