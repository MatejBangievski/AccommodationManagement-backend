package com.example.emtlab.selenium.tests;

import com.example.emtlab.selenium.pages.AccommodationsPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccommodationsPageTests {

    private static WebDriver driver;
    private static AccommodationsPage accommodationsPage;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();

        accommodationsPage = new AccommodationsPage(driver);
        accommodationsPage.openPage();
    }

    @Test
    @Order(1)
    public void testAddAccommodation() {
        accommodationsPage.clickAddAccommodation();
        accommodationsPage.fillAccommodationForm("Test Accommodation", "3");
        accommodationsPage.confirmAdd();

        Assertions.assertTrue(accommodationsPage.isAccommodationVisible("Test Accommodation"));
    }

    @Test
    @Order(2)
    public void testEditAccommodation() {
        accommodationsPage.clickEdit("Test Accommodation");
        accommodationsPage.editAccommodationName("Updated Accommodation");

        Assertions.assertTrue(accommodationsPage.isAccommodationVisible("Updated Accommodation"));
    }

    @Test
    @Order(3)
    public void testDeleteAccommodation() {
        accommodationsPage.clickDelete("Updated Accommodation");
        accommodationsPage.confirmDelete();

        Assertions.assertFalse(accommodationsPage.isAccommodationVisible("Updated Accommodation"));
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}