package me.jacobtread.tells;

import org.openqa.selenium.By;

public class TellConstants {

    /**
     * The base path url of Tellonym.me
     */
    public static final String BASE_URL = "https://tellonym.me";
    /**
     * The url format for accounts
     */
    public static final String ACCOUNT_URL = BASE_URL + "/%s";
    /**
     * The version of the bot
     */
    public static final String VERSION = "1.0.0";


    /**
     * The css selector for the Avatar <img> element
     * (For checking if the element exists)
     */
    public static final By AVATAR_SELECTOR = By.cssSelector(".rmq-f5f56a03 " +
            "> div:nth-child(1) " +
            "> div:nth-child(3) " +
            "> div:nth-child(1) " +
            "> div:nth-child(1) " +
            "> div:nth-child(1) " +
            "> div:nth-child(1) " +
            "> img:nth-child(1)"
    );
    /**
     * The css selector for the Text Area containing the tell message
     * (For checking if the element exists and entering the message)
     */
    public static final By TEXT_AREA_SELECTOR = By.cssSelector(".rmq-f5f56a03 " +
            "> div:nth-child(1) " +
            "> div:nth-child(4) " +
            "> div:nth-child(1) " +
            "> div:nth-child(1) " +
            "> textarea:nth-child(1)");

    /**
     * The css selector for the Submit Button
     * (For submitting the message)
     */
    public static final By SUBMIT_SELECTOR = By.cssSelector(".rmq-f5f56a03 " +
            "> div:nth-child(1) " +
            "> div:nth-child(4) " +
            "> div:nth-child(3) " +
            "> div:nth-child(1) " +
            "> div:nth-child(2) " +
            "> form:nth-child(1) " +
            "> button:nth-child(1)");

    /**
     * The time to wait before rechecking if elements have loaded (seconds)
     */
    public static final int WAIT_TIME = 5;


}
