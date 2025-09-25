package com.example.emtlab.selenium.tests;

import com.example.emtlab.selenium.pages.HostsPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HostsPageTests {

    private static WebDriver driver;
    private static HostsPage hostsPage;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        hostsPage = new HostsPage(driver);
        hostsPage.openPage();
    }

    @Test
    @Order(1)
    public void testAddHost() {
        hostsPage.clickAddHost();
        hostsPage.fillAddHostForm("Carl", "Johnson");
        hostsPage.confirmAdd();
        assertTrue(hostsPage.isHostPresent("Carl Johnson"));
    }

    @Test
    @Order(2)
    public void testEditHost() {
        hostsPage.clickEdit("Carl Johnson");
        hostsPage.fillEditHostForm("Niko", "Belic");
        hostsPage.confirmEdit();
        assertTrue(hostsPage.isHostPresent("Niko Belic"));
    }

    @Test
    @Order(3)
    public void testDeleteHost() {
        hostsPage.clickDelete("Niko Belic");
        hostsPage.confirmDelete();
        assertFalse(hostsPage.isHostPresent("Niko Belic"));
    }

    @AfterAll
    public static void teardown() {
        driver.quit();
    }
}
