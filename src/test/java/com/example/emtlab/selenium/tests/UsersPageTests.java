package com.example.emtlab.selenium.tests;

import com.example.emtlab.selenium.pages.UsersPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersPageTests {
    private static WebDriver driver;
    private static UsersPage usersPage;
    private final String testUser = "user";

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        usersPage = new UsersPage(driver);
        usersPage.openPage();
    }

    @Test @Order(1)
    void testReserve() {
        usersPage.clickReserve(testUser);
        usersPage.confirmReserveFirst();
        assertFalse(usersPage.hasNoReservations(testUser));
    }

    @Test @Order(2)
    void testBook() {
        usersPage.clickBook(testUser);
        usersPage.confirmBookFirst();
        assertFalse(usersPage.hasNoBookings(testUser));
    }

    @Test @Order(3)
    void testCompleteStay() {
        usersPage.clickComplete(testUser);
        usersPage.confirmCompleteFirst();
        assertTrue(usersPage.hasNoBookings(testUser));
    }

    @Test @Order(4)
    void testCancelReservation() {
        usersPage.clickReserve(testUser);
        usersPage.confirmReserveFirst();
        assertFalse(usersPage.hasNoReservations(testUser));

        usersPage.clickCancel(testUser);
        usersPage.confirmCancelFirst();
        assertTrue(usersPage.hasNoReservations(testUser));
    }


    @Test @Order(5)
    void testReserveAll() {
        usersPage.clickReserveAll(testUser);
        assertFalse(usersPage.hasNoReservations(testUser));
    }

    @Test @Order(6)
    void testBookAll() {
        usersPage.clickBookAll(testUser);
        assertFalse(usersPage.hasNoBookings(testUser));
    }

    @Test @Order(7)
    void testCompleteAll() {
        usersPage.clickCompleteAll(testUser);
        assertTrue(usersPage.hasNoBookings(testUser));
    }

    @Test @Order(8)
    void testCancelAllReservations() {
        usersPage.clickReserveAll(testUser);
        usersPage.clickCancelAll(testUser);
        assertTrue(usersPage.hasNoReservations(testUser));
    }

    @AfterAll
    public static void teardown() {
        driver.quit();
    }
}
