package me.jacobtread.tells;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.jacobtread.tells.supplier.MessageSupplier;
import me.jacobtread.tells.supplier.suppliers.FileMessageSupplier;
import me.jacobtread.tells.supplier.suppliers.SingleMessageSupplier;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = TellonymBot.LOGGER;
        OptionParser optionParser = new OptionParser();
        OptionSpec<?> helpOption = optionParser.acceptsAll(Arrays.asList("h", "help"), "Displays the help message").forHelp();

        OptionSpec<String> usernameOption = optionParser.acceptsAll(Arrays.asList("u", "username", "user"), "The username of the account to target")
                .withRequiredArg()
                .required()
                .ofType(String.class);
        OptionSpec<?> headOption = optionParser.acceptsAll(Arrays.asList("h", "head"), "Run with browser window head..? why would you want that..");
        OptionSpec<Integer> maxOption = optionParser.acceptsAll(Arrays.asList("m", "max"), "The maximum amount of messages to send")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(-1);
        OptionSpec<File> fileOption = optionParser.acceptsAll(Arrays.asList("f", "file"), "A File to read the messages from")
                .withRequiredArg()
                .ofType(File.class);
        OptionSpec<?> randomOption = optionParser.acceptsAll(Arrays.asList("r", "random"), "Whether or not to randomly read from the provided messages")
                .availableIf(fileOption);
        OptionSpec<String> messageOption = optionParser.acceptsAll(Arrays.asList("c", "content"), "A single message to repeatedly send")
                .availableUnless(fileOption)
                .withRequiredArg()
                .ofType(String.class);
        OptionSpec<File> logOption = optionParser.acceptsAll(Arrays.asList("l", "log"), "Specify a log file for logs")
                .withRequiredArg()
                .ofType(File.class);
        OptionSpec<?> quietOption = optionParser.acceptsAll(Arrays.asList("q", "quiet"), "Disable console output")
                .availableIf(fileOption);
        OptionSpec<String> proxyOption = optionParser.acceptsAll(Arrays.asList("p", "proxy"), "Specify a second proxy layer formatted as: host:port (to bypass being blocked by the user) ")
                .withRequiredArg()
                .ofType(String.class);
        OptionSet optionSet = optionParser.parse(args);
        if (optionSet.has(quietOption)) {
            TellonymBot.DISABLE_LOGGING = true;
        }
        logger.finest(String.format("" +
                " _______         __ __                                               \n" +
                "|_     _|.-----.|  |  |.-----.-----.--.--.--------.  .--------.-----.\n" +
                "  |   |  |  -__||  |  ||  _  |     |  |  |        |__|        |  -__|\n" +
                "  |___|  |_____||__|__||_____|__|__|___  |__|__|__|__|__|__|__|_____|\n" +
                "                                   |_____|                           \n" +
                " ______         __                                                   \n" +
                "|   __ \\.-----.|  |_                                                 \n" +
                "|   __ <|  _  ||   _|     Version: %s                                 \n" +
                "|______/|_____||____|     Use '--help' for arguments and descriptions\n\n", TellConstants.VERSION));
        if (optionSet.has(helpOption)) {
            try {
                optionParser.printHelpOn(System.out);
            } catch (IOException ignored) {
            }
            return;
        }
        if (optionSet.has(logOption)) {
            File logPath = logOption.value(optionSet);
            try {
                logger.addHandler(new FileHandler(logPath.getAbsolutePath()));
            } catch (IOException e) {
                logger.log(Level.WARNING, "Unable to add file logger continuing anyway", e);
            }
        }
        String username = usernameOption.value(optionSet);
        boolean head = optionSet.has(headOption);
        int max = maxOption.value(optionSet);
        MessageSupplier messages;
        if (optionSet.has(fileOption)) {
            Path file = fileOption.value(optionSet).toPath();
            if (!Files.exists(file)) {
                logger.severe("");
            }
            boolean random = optionSet.has(randomOption);
            FileMessageSupplier supplier = new FileMessageSupplier(file, max, random);
            logger.info("Loading messages");
            try {
                supplier.load();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Unable to load messages file", e);
                return;
            }
            if (supplier.hasMore()) {
                logger.severe("File did not contain any messages??");
                return;
            }
            messages = supplier;
        } else {
            String message = messageOption.value(optionSet);
            messages = new SingleMessageSupplier(message, max);
        }
        if (optionSet.has(proxyOption)) {
            String proxy = proxyOption.value(optionSet);
            String[] parts = proxy.split(":");
            if (parts.length > 1) {
                try {
                    InetSocketAddress address = InetSocketAddress.createUnresolved(parts[0], Integer.parseInt(parts[1]));
                    ProxyController.setChainedProxy(address);
                } catch (NumberFormatException e) {
                    logger.log(Level.SEVERE, "Unable to get port from proxy string invalid int");
                }
            } else {
                logger.severe("Proxy missing port!");
            }
        }
        TellonymBot bot = new TellonymBot(username, !head, messages);
        bot.run();
    }

}
