import me.jacobtread.tells.TellonymBot;
import me.jacobtread.tells.suppliers.FileMessageSupplier;

import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    public static void main(String[] args) {
        String username = "example";
        TellonymBot bot = new TellonymBot(username,  new FileMessageSupplier(Paths.get("/path/to/your/file")));
        bot.run();
    }

}
