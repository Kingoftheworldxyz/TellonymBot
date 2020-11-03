package me.jacobtread.tells;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static me.jacobtread.tells.TellConstants.*;

public class TellonymBot {

    public static final Logger LOGGER = Logger.getLogger("TellonymBot");
    public static boolean DISABLE_LOGGING = false;
    public static BrowserMobProxyServer PROXY;

    static {
        PROXY = new BrowserMobProxyServer();
        PROXY.addRequestFilter((request, contents, messageInfo) -> {
            String url = messageInfo.getUrl();
            if (
                    url.contains("c.amazon-adsystem.com")
                            || url.contains("securepubads.g.doubleclick.net")
                            || url.endsWith(".css")
                            || url.endsWith(".png")
                            || url.endsWith(".jpg")
                            || url.contains("apex.go.sonobi.com")
                            || url.contains("ads.pubmatic.com")
                            || url.contains("facebook.com")
                            || url.contains("google-analytics")
            ) {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.NOT_FOUND);
            }
            return null;
        });
        PROXY.start(0);
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new LogHandler());
        Path driverPath = DriverUtils.getDriver(false);
        if (driverPath == null) {
            System.exit(1);
        }
        System.setProperty("webdriver.gecko.driver", driverPath.toAbsolutePath().toString());
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
    }

    private final String username;
    private final boolean isHeadless;
    private final String accountUrl;
    private WebDriverWait waitTime;
    private Supplier<String> messages;
    private WebDriver driver;
    private boolean isRunning;

    public TellonymBot(String username, Supplier<String> messages) {
        this(username, true, messages);
    }

    public TellonymBot(String username, boolean isHeadless, Supplier<String> messages) {
        this.username = username;
        this.isHeadless = isHeadless;
        this.messages = messages;
        accountUrl = String.format(ACCOUNT_URL, username);
    }

    public void load() {
        LOGGER.info("Creating driver instance");
        FirefoxOptions options = new FirefoxOptions();
        Proxy proxy = new Proxy();
        proxy.setHttpProxy("localhost:" + PROXY.getPort());
        proxy.setSslProxy("localhost:" + PROXY.getPort());
        options.setProxy(proxy);
        options.setHeadless(isHeadless);
        driver = new FirefoxDriver(options);
        waitTime = new WebDriverWait(driver, WAIT_TIME);
        driver.manage().timeouts().setScriptTimeout(1, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void run() {
        LOGGER.info(String.format("== Starting Bot [username=%s] [headless=%b] ==", username, isHeadless));
        load();
        boolean exists = checkUserExists();
        if (exists) {
            LOGGER.info("Found user " + username);
            isRunning = true;
            int attempt = 0;
            while (isRunning) {
                String message = messages.get();
                if (message == null) {
                    isRunning = false;
                    break;
                } else if (message.length() < 4) {
                    LOGGER.warning("\nMessage too short minimum 4 chars");
                } else if (message.length() > 15000) {
                    LOGGER.warning("\nMessage too large maximum 15k chars");
                } else {
                    attempt++;
                    LOGGER.info("\r\rSending message [" + attempt + "]");
                    send(message);
                }
            }
            LOGGER.info("\nEnded message supply/reached amount exiting");
            stop();
        } else {
            LOGGER.warning("User " + " does not exist bot exiting...");
        }
    }

    public void stop() {
        isRunning = false;
        driver.quit();
        try {
            PROXY.abort();
        } catch (Exception ignored) {

        }
    }

    private void send(String message) {
        if (!driver.getCurrentUrl().equals(accountUrl)) {
            driver.get(accountUrl);
        }
        waitLoaded();
        waitTillPresent(TEXT_AREA_SELECTOR);
        waitTillPresent(SUBMIT_SELECTOR);
        WebElement textArea = driver.findElement(TEXT_AREA_SELECTOR);
        WebElement submit = driver.findElement(SUBMIT_SELECTOR);
        textArea.click();
        if (message == null) {
            LOGGER.warning("Unable to send null text...");
        } else {
            textArea.sendKeys(message);
            submit.click();
        }
        try {
            waitTime.withTimeout(Duration.ofSeconds(30)).until(webDriver -> webDriver.getCurrentUrl().endsWith("/sent"));
        } catch (TimeoutException e) {
            LOGGER.warning("Skipping attempt failed to check if sent...");
        }
    }

    public boolean checkUserExists() {
        driver.get(accountUrl);
        waitLoaded();
        AtomicBoolean isInvalid = new AtomicBoolean(false);
        waitTime.until(webDriver -> {
            if (!webDriver.getCurrentUrl().equals(accountUrl)) {
                isInvalid.set(true);
                return true;
            }
            return driver.findElements(AVATAR_SELECTOR).size() >= 1;
        });
        return !isInvalid.get();
    }

    private void waitTillPresent(By by) {
        waitTime.until(webDriver -> webDriver.findElements(by).size() > 0);
    }

    private void waitLoaded() {
        waitTime.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState")
                .equals("complete")
        );
    }

    public void setMessages(Supplier<String> messages) {
        this.messages = messages;
    }

    public String getUsername() {
        return username;
    }
}
