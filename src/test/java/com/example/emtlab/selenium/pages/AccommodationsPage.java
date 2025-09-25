package com.example.emtlab.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AccommodationsPage extends AbstractPage {

    @FindBy(xpath = "//button[contains(text(),'Add Accommodation')]")
    private WebElement addAccommodationButton;

    public AccommodationsPage(WebDriver driver) {
        super(driver);
    }

    public void openPage() {
        open("accommodations");
        wait.until(ExpectedConditions.visibilityOf(addAccommodationButton));
    }

    public void clickAddAccommodation() {
        addAccommodationButton.click();
    }

    public void fillAccommodationForm(String name, String numRooms) {
        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(text(),'Name')]/following::input[1]")));
        nameField.clear();
        nameField.sendKeys(name);


        WebElement categorySelect = driver.findElement(By.xpath("//label[contains(text(),'Category')]/following::div"));
        categorySelect.click();
        WebElement firstCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ul[@role='listbox']//li[1]")));
        firstCategory.click();

        WebElement hostSelect = driver.findElement(By.xpath("//label[contains(text(),'Host')]/following::div"));
        hostSelect.click();
        WebElement firstHost = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ul[@role='listbox']//li[1]")));
        firstHost.click();

        WebElement roomsField = driver.findElement(By.xpath("//label[contains(text(),'Number of Rooms')]/following::input[1]"));
        roomsField.clear();
        roomsField.sendKeys(numRooms);
    }

    public void confirmAdd() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement addBtn = dialog.findElement(By.xpath(".//button[contains(text(),'Add')]"));
        wait.until(ExpectedConditions.elementToBeClickable(addBtn));
        addBtn.click();
    }

    public void clickEdit(String name) {
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h5[contains(text(),'" + name + "')]/ancestor::div[contains(@class,'MuiCard-root')]//button[contains(text(),'Edit')]")));
        editBtn.click();
    }

    public void editAccommodationName(String newName) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement nameField = dialog.findElement(By.xpath(".//label[contains(text(),'Name')]/following::input[1]"));

        nameField.click();
        nameField.sendKeys(Keys.CONTROL + "a");
        nameField.sendKeys(Keys.BACK_SPACE);
        nameField.sendKeys(newName);

        WebElement editBtn = dialog.findElement(By.xpath(".//button[contains(text(),'Edit')]"));
        wait.until(ExpectedConditions.elementToBeClickable(editBtn));
        editBtn.click();
    }

    public void clickDelete(String name) {
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h5[contains(text(),'" + name + "')]/ancestor::div[contains(@class,'MuiCard-root')]//button[contains(text(),'Delete')]")));
        deleteBtn.click();
    }

    public void confirmDelete() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement confirmDelete = dialog.findElement(By.xpath(".//button[normalize-space()='Delete']"));
        wait.until(ExpectedConditions.elementToBeClickable(confirmDelete));
        confirmDelete.click();
    }

    public boolean isAccommodationVisible(String name) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h5[contains(text(),'" + name + "')]")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
