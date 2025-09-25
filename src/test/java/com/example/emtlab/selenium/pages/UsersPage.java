package com.example.emtlab.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class UsersPage extends AbstractPage {

    private final String url = "http://localhost:3000/users";

    public UsersPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void openPage() {
        open("users");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h4[normalize-space()='Users']")));
    }

    private WebElement getUserCard(String username) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[normalize-space()='Username: " + username + "']/ancestor::div[contains(@class,'MuiCard-root')]")
        ));
    }

    private List<WebElement> getReservations(String username) {
        WebElement card = getUserCard(username);
        return card.findElements(By.xpath(".//h6[contains(text(),'Reservations:')]/following::p[contains(text(),'•')]"));
    }

    private List<WebElement> getBookings(String username) {
        WebElement card = getUserCard(username);
        return card.findElements(By.xpath(".//h6[contains(text(),'Bookings:')]/following::p[contains(text(),'•')]"));
    }

    protected void clickDialogButton(String buttonText) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));
        WebElement btn = dialog.findElement(
                By.xpath(".//button[contains(text(),'" + buttonText + "')]")
        );
        wait.until(ExpectedConditions.elementToBeClickable(btn));
        btn.click();
    }

    public void clickReserve(String username) {
        getUserCard(username)
                .findElement(By.xpath(".//button[normalize-space()='Reserve']"))
                .click();
    }

    public void clickBook(String username) {
        getUserCard(username)
                .findElement(By.xpath(".//button[normalize-space()='Book']"))
                .click();
    }

    public void clickComplete(String username) {
        getUserCard(username)
                .findElement(By.xpath(".//button[normalize-space()='Complete']"))
                .click();
    }

    public void clickCancel(String username) {
        getUserCard(username)
                .findElement(By.xpath(".//button[normalize-space()='Cancel']"))
                .click();
    }

    public void clickReserveAll(String username) {
        WebElement card = getUserCard(username);
        card.findElement(By.xpath(".//button[contains(text(),'Reserve All')]")).click();
        wait.until(d -> !getReservations(username).isEmpty()); // re-check dynamically
    }

    public void clickBookAll(String username) {
        WebElement card = getUserCard(username);
        card.findElement(By.xpath(".//button[contains(text(),'Book All')]")).click();
        wait.until(d -> !getBookings(username).isEmpty()); // always re-locate
    }

    public void clickCompleteAll(String username) {
        WebElement card = getUserCard(username);
        card.findElement(By.xpath(".//button[contains(text(),'Complete All')]")).click();
        wait.until(d -> getBookings(username).isEmpty());
    }

    public void clickCancelAll(String username) {
        WebElement card = getUserCard(username);
        card.findElement(By.xpath(".//button[contains(text(),'Cancel All')]")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@role='dialog']")));
        wait.until(d -> getReservations(username).isEmpty());
    }

    public void confirmReserveFirst() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement accommodationSelect = driver.findElement(
                By.xpath("//label[contains(text(),'Select Accommodation')]/following::div")
        );
        accommodationSelect.click();

        WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ul[@role='listbox']//li[1]")));
        firstOption.click();

        clickDialogButton("Reserve");
        wait.until(ExpectedConditions.invisibilityOf(dialog));
    }

    public void confirmBookFirst() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                dialog.findElement(By.xpath(".//div[@role='button'][1]"))));
        firstOption.click();

        clickDialogButton("Book");
        wait.until(ExpectedConditions.invisibilityOf(dialog));
    }

    public void confirmCompleteFirst() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                dialog.findElement(By.xpath(".//div[@role='button'][1]"))));
        firstOption.click();

        clickDialogButton("Complete Stay");
        wait.until(ExpectedConditions.invisibilityOf(dialog));
    }

    public void confirmCancelFirst() {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog']")));

        WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                dialog.findElement(By.xpath(".//div[@role='button'][1]"))));
        firstOption.click();

        clickDialogButton("Cancel");
        wait.until(ExpectedConditions.invisibilityOf(dialog));
    }


    public boolean hasNoReservations(String username) {
        return getReservations(username).isEmpty();
    }

    public boolean hasNoBookings(String username) {
        return getBookings(username).isEmpty();
    }
}
