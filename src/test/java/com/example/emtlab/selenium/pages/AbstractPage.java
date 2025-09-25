package com.example.emtlab.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/*
    For the following tests:
    * There must be a User with username - user
    * It's assumed that everything is in its default state (nothing reserved, booked etc.)
 */

public abstract class AbstractPage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:3000/";

    public AbstractPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void open(String path) {
        driver.get(BASE_URL + path);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }
}