package me.jacobtread.tells;

import me.jacobtread.tells.supplier.MessageSupplier;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static me.jacobtread.tells.TellConstants.*;

public class TellonymBot {

    public static final Logger LOGGER = Logger.getLogger("TellonymBot");
    public static boolean DISABLE_LOGGING = false;

    static {
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
    private MessageSupplier messages;
    private WebDriver driver;
    private boolean isRunning;

    public TellonymBot(String username, MessageSupplier messages) {
        this(username, true, messages);
    }

    /**
     * @param username The username of the account to send the message to
     * @param isHeadless Setting to false will display the firefox window while attacking
     * @param messages The message supplier
     */
    public TellonymBot(String username, boolean isHeadless, MessageSupplier messages) {
        this.username = username;
        this.isHeadless = isHeadless;
        this.messages = messages;
        accountUrl = String.format(ACCOUNT_URL, username);
    }

    /**
     * Creates the driver instance and attaches proxy
     */
    public void load() {
        LOGGER.info("Creating driver instance");
        FirefoxOptions options = new FirefoxOptions();
        options.setProxy(ProxyController.getSeleniumProxy());
        options.setHeadless(isHeadless);
        driver = new FirefoxDriver(options);
        waitTime = new WebDriverWait(driver, WAIT_TIME);
        driver.manage().timeouts().setScriptTimeout(1, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    /**
     * Starts the spam
     */
    public void run() {
        LOGGER.info(String.format("== Starting Bot [username=%s] [headless=%b] ==", username, isHeadless));
        load();
        boolean exists = isExistingAccount();
        if (exists) {
            LOGGER.info("Found user " + username);
            isRunning = true;
            int attempt = 0;
            while (isRunning) {
                if (!messages.hasMore()) {
                    LOGGER.info("Completed. Sent all messages");
                    break;
                }
                String message = messages.next();
                if (message.length() < 4) {
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

    /**
     * Stops the bot and shuts down the proxy server;
     */
    public void stop() {
        isRunning = false;
        driver.quit();
    }

    /**
     * Sends the message to tellonym.me
     *
     * @param message The message to send
     */
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

    /**
     * Loads the account url to check if the account actually exists
     * (If the account does not exist the bot is redirected to the base url)
     *
     * @return Whether or not the provided account exists
     */
    public boolean isExistingAccount() {
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

    /**
     * Waits until the specific element is on the page
     *
     * @param by The element to wait for
     */
    private void waitTillPresent(By by) {
        waitTime.until(webDriver -> webDriver.findElements(by).size() > 0);
    }

    /**
     * Waits until the page is fully loaded
     * (Uses JS to request document.readyState and completes
     */
    private void waitLoaded() {
        waitTime.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState")
                .equals("complete")
        );
    }

    /**
     * Replace the current messages supplier with a new one
     *
     * @param messages The new messages supplier
     */
    public void setMessages(MessageSupplier messages) {
        this.messages = messages;
    }


}
