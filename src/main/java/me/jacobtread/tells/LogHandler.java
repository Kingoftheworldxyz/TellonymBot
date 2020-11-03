package me.jacobtread.tells;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {
    @Override
    public void publish(LogRecord logRecord) {
        if (TellonymBot.DISABLE_LOGGING) {
            return;
        }
        PrintStream out;
        Level level = logRecord.getLevel();
        String message = logRecord.getMessage();
        if (Level.SEVERE.equals(level)) {
            out = System.err;
        } else {
            out = System.out;
        }
        if (level == Level.FINEST) {
            out.print(message);
        } else {
            if (level == Level.WARNING) {
                message = "\u001B[33m" + message;
            }
            out.println(message);
        }
        Throwable thrown = logRecord.getThrown();
        if (thrown != null) {
            thrown.printStackTrace();
        }
        if (level == Level.SEVERE) {
            System.exit(1);
        }
    }

    @Override
    public void flush() {
        System.out.flush();
        System.err.flush();
    }

    @Override
    public void close() throws SecurityException {
        flush();
    }
}
