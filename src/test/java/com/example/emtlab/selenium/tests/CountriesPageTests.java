package com.example.emtlab.selenium.tests;

import com.example.emtlab.selenium.pages.CountriesPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CountriesPageTests {

    private static WebDriver driver;
    private static CountriesPage countriesPage;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        countriesPage = new CountriesPage(driver);
        countriesPage.open();
    }

    @Test
    @Order(1)
    public void testAddCountry() {
        countriesPage.clickAddCountry();
        countriesPage.fillAddCountryForm("TestLand", "Europe");
        countriesPage.confirmAdd();
        assertTrue(countriesPage.isCountryPresent("TestLand"));
    }

    @Test
    @Order(2)
    public void testEditCountry() {
        countriesPage.clickEditFirstCountry();
        countriesPage.fillEditCountryForm("Updatedland", "Utopia");
        countriesPage.confirmEdit();
        assertTrue(countriesPage.isCountryPresent("Updatedland"));
    }

    @Test
    @Order(3)
    public void testDeleteCountry() {
        countriesPage.clickDeleteFirstCountry();
        countriesPage.confirmDelete();
        assertFalse(countriesPage.isCountryPresent("Updatedland"));
    }

    @AfterAll
    public static void teardown() {
        driver.quit();
    }
}
