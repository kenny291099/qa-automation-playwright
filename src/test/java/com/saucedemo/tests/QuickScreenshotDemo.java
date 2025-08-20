package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.CartPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Quick and simple failing tests for screenshot demonstration.
 * 
 * USAGE: Remove @Disabled annotation from any test to enable it for demo.
 * These tests are designed to fail quickly and show clear visual states.
 */
@Epic("Sauce Demo E-commerce")
@Feature("Quick Screenshot Demo")
@Owner("QA Team")
public class QuickScreenshotDemo extends BaseTest {

    @Test
    @DisplayName("QUICK DEMO: Wrong login credentials")
    @Description("Quick failing test - shows login error message")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Login Screenshot")
    public void quickLoginFailDemo() {
        LoginPage loginPage = new LoginPage();
        loginPage.loginWithInvalidCredentials("wrong_user", "wrong_pass");
        
        // This will fail and show login page with error
        assertFalse(loginPage.isErrorMessageDisplayed(), 
                "DEMO FAIL: This captures login page with error message");
    }

    @Test
    @DisplayName("QUICK DEMO: Wrong product count")
    @Description("Quick failing test - shows inventory page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Inventory Screenshot")
    public void quickInventoryFailDemo() {
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        
        // This will fail and show full inventory page
        assertEquals(999, inventoryPage.getProductCount(), 
                "DEMO FAIL: This captures inventory page with all products");
    }

    @Test
    @DisplayName("QUICK DEMO: Cart with items")
    @Description("Quick failing test - shows cart with multiple items")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Screenshot")
    public void quickCartFailDemo() {
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        
        // Add several items to cart
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        
        CartPage cartPage = inventoryPage.clickShoppingCart();
        
        // This will fail and show cart with 3 items
        assertEquals(0, cartPage.getCartItemCount(), 
                "DEMO FAIL: This captures cart page with 3 items");
    }

    @Test
    @DisplayName("QUICK DEMO: Empty fields error")
    @Description("Quick failing test - shows empty form validation")
    @Severity(SeverityLevel.NORMAL)
    @Story("Form Validation Screenshot")
    public void quickFormValidationDemo() {
        LoginPage loginPage = new LoginPage();
        
        // Try to login with empty fields
        loginPage.loginWithInvalidCredentials("", "");
        
        // This will fail and show empty field validation
        assertFalse(loginPage.isErrorMessageDisplayed(), 
                "DEMO FAIL: This captures empty field validation error");
    }

    @Test
    @DisplayName("QUICK DEMO: Always fails for guaranteed screenshot")
    @Description("This test always fails - use for reliable screenshot capture")
    @Severity(SeverityLevel.TRIVIAL)
    @Story("Guaranteed Screenshot")
    public void guaranteedFailForScreenshot() {
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        
        // Add some items for visual interest
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        
        // This will ALWAYS fail - guaranteed screenshot
        assertTrue(false, 
                "GUARANTEED FAILURE: This test always fails to capture current page state");
    }
}
