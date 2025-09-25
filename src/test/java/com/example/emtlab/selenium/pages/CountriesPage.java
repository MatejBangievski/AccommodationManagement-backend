package com.example.emtlab.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class CountriesPage extends AbstractPage {

    private final String url = "http://localhost:3000/countries";

    @FindBy(xpath = "//button[contains(text(),'Add Country')]")
    private WebElement addCountryButton;

    public CountriesPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOf(addCountryButton));
    }

    public void clickAddCountry() {
        wait.until(ExpectedConditions.elementToBeClickable(addCountryButton)).click();
    }

    public void fillAddCountryForm(String name, String continent) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement nameField = dialog.findElement(By.name("name"));
        WebElement continentField = dialog.findElement(By.name("continent"));

        nameField.clear();
        nameField.sendKeys(name);
        continentField.clear();
        continentField.sendKeys(continent);
    }

    public void confirmAdd() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement addBtn = dialog.findElement(By.xpath(".//button[normalize-space()='Add']"));
        wait.until(ExpectedConditions.elementToBeClickable(addBtn)).click();
    }

    public void clickEditFirstCountry() {
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'Edit')])[1]")));
        editBtn.click();
    }

    public void fillEditCountryForm(String newName, String newContinent) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement nameField = dialog.findElement(By.name("name"));
        WebElement continentField = dialog.findElement(By.name("continent"));

        nameField.click();
        nameField.sendKeys(Keys.CONTROL + "a");
        nameField.sendKeys(Keys.BACK_SPACE);
        nameField.sendKeys(newName);

        continentField.click();
        continentField.sendKeys(Keys.CONTROL + "a");
        continentField.sendKeys(Keys.BACK_SPACE);
        continentField.sendKeys(newContinent);
    }

    public void confirmEdit() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement editBtn = dialog.findElement(By.xpath(".//button[normalize-space()='Edit']"));
        wait.until(ExpectedConditions.elementToBeClickable(editBtn)).click();
    }

    public void clickDeleteFirstCountry() {
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'Delete')])[1]")));
        deleteBtn.click();
    }

    public void confirmDelete() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement deleteBtn = dialog.findElement(By.xpath(".//button[normalize-space()='Delete']"));
        wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
    }

    public boolean isCountryPresent(String name) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h5[normalize-space()='" + name + "']")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
