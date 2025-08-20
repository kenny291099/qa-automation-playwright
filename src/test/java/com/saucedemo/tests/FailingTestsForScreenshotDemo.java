package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutPage;
import com.saucedemo.pages.ProductDetailsPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class contains intentionally failing tests to demonstrate
 * the screenshot capture functionality when tests fail.
 * 
 * IMPORTANT: These tests are disabled by default to prevent CI/CD failures.
 * Remove @Disabled annotations to run them for screenshot demonstration.
 */
@Epic("Sauce Demo E-commerce")
@Feature("Screenshot Demonstration")
@Owner("QA Team")
public class FailingTestsForScreenshotDemo extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void setupFailingTests() {
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login("standard_user", "secret_sauce");
    }

    @Test
    @DisplayName("DEMO: Login with incorrect credentials - Screenshot on failure")
    @Description("This test intentionally fails to demonstrate screenshot capture on login failure")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Screenshot Demo - Login Failure")
    public void demoLoginFailureScreenshot() {
        // Start fresh for this demo
        LoginPage loginPage = new LoginPage();
        
        // This will intentionally fail - wrong password
        loginPage.loginWithInvalidCredentials("standard_user", "wrong_password");
        
        // This assertion will fail and trigger a screenshot
        assertFalse(loginPage.isErrorMessageDisplayed(), 
                "INTENTIONAL FAILURE: This should fail to demonstrate screenshot capture");
    }

    @Test
    @DisplayName("DEMO: Product count assertion failure - Screenshot on inventory page")
    @Description("This test intentionally fails to show inventory page state in screenshot")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Screenshot Demo - Inventory Failure")
    public void demoInventoryPageFailureScreenshot() {
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page should be loaded");
        
        // This assertion will intentionally fail to capture inventory page screenshot
        assertEquals(10, inventoryPage.getProductCount(), 
                "INTENTIONAL FAILURE: Expected 10 products but there are only 6 - screenshot will show inventory state");
    }

    @Test
    @DisplayName("DEMO: Cart page assertion failure - Screenshot with cart contents")
    @Description("This test fails on cart page to show cart state in screenshot")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Screenshot Demo - Cart Failure")
    public void demoCartPageFailureScreenshot() {
        // Add some items to cart first
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        
        CartPage cartPage = inventoryPage.clickShoppingCart();
        assertTrue(cartPage.isCartPageLoaded(), "Cart page should be loaded");
        
        // This will fail and show cart with 2 items in screenshot
        assertEquals(5, cartPage.getCartItemCount(), 
                "INTENTIONAL FAILURE: Expected 5 items but cart has only 2 - screenshot will show cart contents");
    }

    @Test
    @DisplayName("DEMO: Product details page failure - Screenshot with product information")
    @Description("This test fails on product details to show product information in screenshot")
    @Severity(SeverityLevel.NORMAL)
    @Story("Screenshot Demo - Product Details Failure")
    public void demoProductDetailsFailureScreenshot() {
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName("Sauce Labs Backpack");
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Product details page should be loaded");
        
        // This will fail and show product details page in screenshot
        assertEquals("Sauce Labs Laptop", productDetailsPage.getProductName(), 
                "INTENTIONAL FAILURE: Expected 'Laptop' but got 'Backpack' - screenshot will show product details");
    }

    @Test
    @DisplayName("DEMO: Checkout form validation failure - Screenshot with form state")
    @Description("This test fails during checkout to show checkout form state")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Screenshot Demo - Checkout Failure")
    public void demoCheckoutFormFailureScreenshot() {
        // Add item and navigate to checkout
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = inventoryPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        
        assertTrue(checkoutPage.isCheckoutInformationPageLoaded(), "Checkout page should be loaded");
        
        // Fill partial form to show form state in screenshot
        checkoutPage.enterFirstName("John").enterLastName("Doe");
        
        // This will fail and show checkout form with partial data
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), 
                "INTENTIONAL FAILURE: Should be on information page, not overview - screenshot shows form state");
    }

    @Test
    @DisplayName("DEMO: Price comparison failure - Screenshot showing price mismatch")
    @Description("This test fails on price validation to show pricing information")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Screenshot Demo - Price Validation Failure")
    public void demoPriceValidationFailureScreenshot() {
        String actualPrice = inventoryPage.getProductPrice("Sauce Labs Backpack");
        
        // This will fail and show inventory page with actual prices
        assertEquals("$15.99", actualPrice, 
                "INTENTIONAL FAILURE: Expected $15.99 but got different price - screenshot shows actual pricing");
    }

    @Test
    @DisplayName("DEMO: Shopping cart badge failure - Screenshot with cart state")
    @Description("This test fails on cart badge validation to show cart indicator")
    @Severity(SeverityLevel.NORMAL)
    @Story("Screenshot Demo - Cart Badge Failure")
    public void demoCartBadgeFailureScreenshot() {
        // Add items to cart
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        
        // This will fail and show inventory page with cart badge displaying "3"
        assertEquals(1, inventoryPage.getCartItemCount(), 
                "INTENTIONAL FAILURE: Expected 1 item but cart has 3 - screenshot shows cart badge with count");
    }

    @Test
    @DisplayName("DEMO: Product sorting failure - Screenshot with sorted products")
    @Description("This test fails on sorting validation to show product order")
    @Severity(SeverityLevel.NORMAL)
    @Story("Screenshot Demo - Sorting Failure")
    public void demoProductSortingFailureScreenshot() {
        // Sort products by price (low to high)
        inventoryPage.sortProducts("lohi");
        
        // Get the first product name after sorting
        String firstProductName = inventoryPage.getProductNames().get(0);
        
        // This will likely fail and show the actual sorted order
        assertEquals("Sauce Labs Backpack", firstProductName, 
                "INTENTIONAL FAILURE: Expected Backpack to be first after price sorting - screenshot shows actual order");
    }

    @Test
    @DisplayName("DEMO: Menu functionality failure - Screenshot with menu state")
    @Description("This test fails during menu interaction to show menu state")
    @Severity(SeverityLevel.MINOR)
    @Story("Screenshot Demo - Menu Failure")
    public void demoMenuFailureScreenshot() {
        // Open menu
        inventoryPage.openMenu();
        
        // Wait for menu to open
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        // This assertion will fail to show menu in open state
        assertFalse(true, 
                "INTENTIONAL FAILURE: This always fails to capture screenshot with menu open");
    }

    @Test
    @DisplayName("DEMO: Complex workflow failure - Screenshot at specific workflow step")
    @Description("This test fails mid-workflow to show complex page state")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Screenshot Demo - Workflow Failure")
    public void demoComplexWorkflowFailureScreenshot() {
        // Start a complex workflow
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        
        // Navigate to product details
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName("Sauce Labs Bolt T-Shirt");
        productDetailsPage.addToCart();
        
        // Navigate to cart
        CartPage cartPage = productDetailsPage.clickShoppingCart();
        
        // Start checkout process
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        
        // This will fail at checkout overview page, showing the complete order summary
        assertEquals("Checkout: Information", checkoutPage.getCheckoutTitle(), 
                "INTENTIONAL FAILURE: Should show 'Information' but showing 'Overview' - screenshot shows order summary");
    }

    @Test
    @DisplayName("DEMO: Element text mismatch - Screenshot highlighting text content")
    @Description("This test fails on text validation to show actual vs expected content")
    @Severity(SeverityLevel.NORMAL)
    @Story("Screenshot Demo - Text Validation Failure")
    public void demoTextValidationFailureScreenshot() {
        String logoText = inventoryPage.getAppLogoText();
        
        // This will fail and show the actual logo text in the screenshot
        assertEquals("SauceLabs Store", logoText, 
                "INTENTIONAL FAILURE: Expected 'SauceLabs Store' but got different text - screenshot shows actual logo");
    }

    @Test
    @DisplayName("DEMO: Multi-step process failure - Screenshot at failure point")
    @Description("This test fails during a multi-step process to show state at failure")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Screenshot Demo - Multi-step Failure")
    public void demoMultiStepProcessFailureScreenshot() {
        // Step 1: Add multiple products
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        inventoryPage.addProductToCart("Sauce Labs Fleece Jacket");
        
        // Step 2: Navigate to cart
        CartPage cartPage = inventoryPage.clickShoppingCart();
        
        // Step 3: Remove some items
        cartPage.removeItemFromCart("Sauce Labs Bike Light");
        cartPage.removeItemFromCart("Sauce Labs Bolt T-Shirt");
        
        // Step 4: This assertion will fail, showing cart with remaining items
        assertEquals(1, cartPage.getCartItemCount(), 
                "INTENTIONAL FAILURE: Expected 1 item after removals but have 2 - screenshot shows final cart state");
    }
}
