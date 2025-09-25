package com.example.emtlab.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HostsPage extends AbstractPage {

    @FindBy(xpath = "//button[contains(text(),'Add Host')]")
    private WebElement addHostButton;

    public HostsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void openPage() {
        open("hosts");
        wait.until(ExpectedConditions.visibilityOf(addHostButton));
    }

    public void clickAddHost() {
        wait.until(ExpectedConditions.elementToBeClickable(addHostButton)).click();
    }

    public void fillAddHostForm(String name, String surname) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement nameField = dialog.findElement(By.xpath(".//label[contains(text(),'Name')]/following::input[1]"));
        WebElement surnameField = dialog.findElement(By.xpath(".//label[contains(text(),'Surname')]/following::input[1]"));

        nameField.clear();
        nameField.sendKeys(name);
        surnameField.clear();
        surnameField.sendKeys(surname);

        // Open country select (scoped to dialog) and pick first option
        WebElement countrySelect = dialog.findElement(By.xpath(".//label[contains(text(),'Country')]/following::div"));
        countrySelect.click();
        WebElement firstCountry = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ul[@role='listbox']//li[1]")));
        firstCountry.click();
    }

    public void confirmAdd() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));
        WebElement addBtn = dialog.findElement(By.xpath(".//button[normalize-space()='Add']"));
        wait.until(ExpectedConditions.elementToBeClickable(addBtn)).click();
    }

    public void clickEdit(String fullName) {
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h5[normalize-space()='" + fullName + "']" +
                        "/ancestor::div[contains(@class,'MuiCard-root')]" +
                        "//button[contains(text(),'Edit')]")));
        editBtn.click();
    }

    public void fillEditHostForm(String newName, String newSurname) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement nameField = dialog.findElement(By.xpath(".//label[contains(text(),'Name')]/following::input[1]"));
        WebElement surnameField = dialog.findElement(By.xpath(".//label[contains(text(),'Surname')]/following::input[1]"));

        // Clear properly for MUI fields
        nameField.click();
        nameField.sendKeys(Keys.CONTROL + "a");
        nameField.sendKeys(Keys.BACK_SPACE);
        nameField.sendKeys(newName);

        surnameField.click();
        surnameField.sendKeys(Keys.CONTROL + "a");
        surnameField.sendKeys(Keys.BACK_SPACE);
        surnameField.sendKeys(newSurname);

        // Re-open country select (scoped) and pick first option
        WebElement countrySelect = dialog.findElement(By.xpath(".//label[contains(text(),'Country')]/following::div"));
        countrySelect.click();
        WebElement firstCountry = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ul[@role='listbox']//li[1]")));
        firstCountry.click();
    }

    public void confirmEdit() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));
        WebElement editBtn = dialog.findElement(By.xpath(".//button[normalize-space()='Edit']"));
        wait.until(ExpectedConditions.elementToBeClickable(editBtn)).click();
    }

    public void clickDelete(String fullName) {
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h5[normalize-space()='" + fullName + "']" +
                        "/ancestor::div[contains(@class,'MuiCard-root')]" +
                        "//button[contains(text(),'Delete')]")));
        deleteBtn.click();
    }

    public void confirmDelete() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));
        WebElement deleteBtn = dialog.findElement(By.xpath(".//button[normalize-space()='Delete']"));
        wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
    }

    public boolean isHostPresent(String fullName) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h5[normalize-space()='" + fullName + "']")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
