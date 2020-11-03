package me.jacobtread.tells;

import org.openqa.selenium.By;

public class TellConstants {

    public static final String BASE_URL = "https://tellonym.me";
    public static final String ACCOUNT_URL = BASE_URL + "/%s";
    public static final String VERSION = "1.0.0";


    public static final By LOADER_SELECTOR = By.cssSelector(".loader");
    public static final By AVATAR_SELECTOR = By.cssSelector(".rmq-f5f56a03 " +
            "> div:nth-child(1) " +
            "> div:nth-child(3) " +
            "> div:nth-child(1) " +
            "> div:nth-child(1) " +
            "> div:nth-child(1) " +
            "> div:nth-child(1) " +
            "> img:nth-child(1)"
    );
    public static final By TEXT_AREA_SELECTOR = By.cssSelector(".rmq-f5f56a03 " +
            "> div:nth-child(1) " +
            "> div:nth-child(4) " +
            "> div:nth-child(1) " +
            "> div:nth-child(1) " +
            "> textarea:nth-child(1)");

    public static final By SUBMIT_SELECTOR = By.cssSelector(".rmq-f5f56a03 " +
            "> div:nth-child(1) " +
            "> div:nth-child(4) " +
            "> div:nth-child(3) " +
            "> div:nth-child(1) " +
            "> div:nth-child(2) " +
            "> form:nth-child(1) " +
            "> button:nth-child(1)");

    public static final int WAIT_TIME = 5;


}
