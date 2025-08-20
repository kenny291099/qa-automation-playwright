package com.saucedemo.tests;

import com.saucedemo.pages.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Sauce Demo E-commerce")
@Feature("Application Navigation")
@Owner("QA Team")
public class NavigationTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void setupNavigationTests() {
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login("standard_user", "secret_sauce");
    }

    @Test
    @DisplayName("Menu open and close functionality")
    @Description("Test opening and closing the hamburger menu")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Menu Navigation")
    public void testMenuOpenAndClose() {
        // Open menu
        inventoryPage.openMenu();
        
        // Wait for menu animation
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        // Close menu
        inventoryPage.closeMenu();
        
        // Verify we're still on inventory page
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Should remain on inventory page after menu operations");
    }

    @Test
    @DisplayName("Logout functionality from menu")
    @Description("Test logging out using the menu option")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Menu Navigation")
    public void testLogoutFromMenu() {
        LoginPage loginPage = inventoryPage.logout();
        
        assertTrue(loginPage.isLoginFormDisplayed(), "Should return to login page");
        assertEquals("Swag Labs", loginPage.getLoginLogoText(), "Login logo should be displayed");
        
        // Verify that we can't access inventory without logging in again
        String currentUrl = loginPage.getCurrentUrl();
        assertTrue(currentUrl.endsWith("index.html") || currentUrl.endsWith("/"), 
                "Should be on login page URL");
    }

    @Test
    @DisplayName("Reset app state functionality")
    @Description("Test reset app state clears cart items")
    @Severity(SeverityLevel.NORMAL)
    @Story("Menu Navigation")
    public void testResetAppStateFromMenu() {
        // Add items to cart first
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        assertEquals(2, inventoryPage.getCartItemCount(), "Cart should have 2 items");
        
        // Reset app state
        inventoryPage.resetAppState();
        
        // Verify cart is cleared
        assertEquals(0, inventoryPage.getCartItemCount(), "Cart should be empty after reset");
        
        // Verify inventory page is still functional
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Should remain on inventory page");
        assertEquals(6, inventoryPage.getProductCount(), "Should still display all products");
    }

    @Test
    @DisplayName("Navigation from inventory to product details")
    @Description("Test navigation flow from inventory to product details page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cross-Page Navigation")
    public void testInventoryToProductDetailsNavigation() {
        String productName = "Sauce Labs Backpack";
        String expectedPrice = inventoryPage.getProductPrice(productName);
        
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName(productName);
        
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Should navigate to product details page");
        assertEquals(productName, productDetailsPage.getProductName(), "Product name should match");
        assertEquals(expectedPrice, productDetailsPage.getProductPrice(), "Price should match");
        
        // Verify back navigation
        InventoryPage returnedInventoryPage = productDetailsPage.goBackToProducts();
        assertTrue(returnedInventoryPage.isInventoryPageLoaded(), "Should return to inventory page");
    }

    @Test
    @DisplayName("Navigation from inventory to cart")
    @Description("Test navigation flow from inventory to cart page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cross-Page Navigation")
    public void testInventoryToCartNavigation() {
        // Add item to cart first
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        assertEquals(1, inventoryPage.getCartItemCount(), "Cart should have 1 item");
        
        CartPage cartPage = inventoryPage.clickShoppingCart();
        
        assertTrue(cartPage.isCartPageLoaded(), "Should navigate to cart page");
        assertEquals(1, cartPage.getCartItemCount(), "Cart should have 1 item");
        assertTrue(cartPage.isItemInCart("Sauce Labs Backpack"), "Item should be in cart");
        
        // Verify back navigation
        InventoryPage returnedInventoryPage = cartPage.continueShopping();
        assertTrue(returnedInventoryPage.isInventoryPageLoaded(), "Should return to inventory page");
        assertEquals(1, returnedInventoryPage.getCartItemCount(), "Cart count should be preserved");
    }

    @Test
    @DisplayName("Navigation through complete shopping flow")
    @Description("Test navigation through the complete shopping journey")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Complete Navigation Flow")
    public void testCompleteShoppingFlowNavigation() {
        // Start at inventory
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Should start on inventory page");
        
        // Navigate to product details
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName("Sauce Labs Backpack");
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Should be on product details");
        
        // Add to cart and navigate to cart
        productDetailsPage.addToCart();
        CartPage cartPage = productDetailsPage.clickShoppingCart();
        assertTrue(cartPage.isCartPageLoaded(), "Should be on cart page");
        
        // Navigate to checkout
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        assertTrue(checkoutPage.isCheckoutInformationPageLoaded(), "Should be on checkout information page");
        
        // Fill information and proceed
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), "Should be on checkout overview page");
        
        // Complete checkout
        checkoutPage.clickFinish();
        assertTrue(checkoutPage.isCheckoutCompletePageLoaded(), "Should be on checkout complete page");
        
        // Return to inventory
        InventoryPage finalInventoryPage = checkoutPage.clickBackHome();
        assertTrue(finalInventoryPage.isInventoryPageLoaded(), "Should return to inventory page");
        assertEquals(0, finalInventoryPage.getCartItemCount(), "Cart should be empty after completion");
    }

    @Test
    @DisplayName("Cart navigation consistency across pages")
    @Description("Test that cart icon navigation is consistent across different pages")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Navigation Consistency")
    public void testCartNavigationConsistencyAcrossPages() {
        // Add items to cart
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        assertEquals(2, inventoryPage.getCartItemCount(), "Cart should have 2 items");
        
        // Test cart navigation from inventory page
        CartPage cartFromInventory = inventoryPage.clickShoppingCart();
        assertEquals(2, cartFromInventory.getCartItemCount(), "Cart should have 2 items from inventory");
        
        // Navigate to product details and test cart navigation
        InventoryPage backToInventory = cartFromInventory.continueShopping();
        ProductDetailsPage productDetailsPage = backToInventory.clickProductName("Sauce Labs Bolt T-Shirt");
        assertEquals(2, productDetailsPage.getCartItemCount(), "Cart count should be preserved on product details");
        
        CartPage cartFromDetails = productDetailsPage.clickShoppingCart();
        assertEquals(2, cartFromDetails.getCartItemCount(), "Cart should have 2 items from product details");
        
        // Verify cart contents are consistent
        assertTrue(cartFromDetails.isItemInCart("Sauce Labs Backpack"), "Backpack should be in cart");
        assertTrue(cartFromDetails.isItemInCart("Sauce Labs Bike Light"), "Bike light should be in cart");
    }

    @ParameterizedTest
    @DisplayName("Product details navigation for all products")
    @Description("Test navigation to product details page for each product")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Navigation")
    @ValueSource(strings = {
        "Sauce Labs Backpack",
        "Sauce Labs Bike Light",
        "Sauce Labs Bolt T-Shirt",
        "Sauce Labs Fleece Jacket",
        "Sauce Labs Onesie",
        "Test.allTheThings() T-Shirt (Red)"
    })
    public void testProductDetailsNavigationForAllProducts(String productName) {
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName(productName);
        
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), 
                "Should navigate to product details for " + productName);
        assertEquals(productName, productDetailsPage.getProductName(), 
                "Product name should match for " + productName);
        
        // Test back navigation
        InventoryPage returnedInventoryPage = productDetailsPage.goBackToProducts();
        assertTrue(returnedInventoryPage.isInventoryPageLoaded(), 
                "Should return to inventory from " + productName + " details");
    }

    @Test
    @DisplayName("URL validation during navigation")
    @Description("Test that URLs change correctly during navigation")
    @Severity(SeverityLevel.MINOR)
    @Story("URL Navigation")
    public void testURLValidationDuringNavigation() {
        // Start on inventory page
        String inventoryUrl = inventoryPage.getCurrentUrl();
        assertTrue(inventoryUrl.contains("inventory.html"), "Should be on inventory page URL");
        
        // Navigate to product details
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName("Sauce Labs Backpack");
        String detailsUrl = productDetailsPage.getCurrentUrl();
        assertTrue(detailsUrl.contains("inventory-item.html"), "Should be on product details URL");
        assertTrue(detailsUrl.contains("id="), "URL should contain product ID");
        
        // Navigate to cart
        CartPage cartPage = productDetailsPage.clickShoppingCart();
        String cartUrl = cartPage.getCurrentUrl();
        assertTrue(cartUrl.contains("cart.html"), "Should be on cart page URL");
        
        // Navigate to checkout
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        String checkoutUrl = checkoutPage.getCurrentUrl();
        assertTrue(checkoutUrl.contains("checkout-step-one.html"), "Should be on checkout information URL");
    }

    @Test
    @DisplayName("Browser back button simulation")
    @Description("Test navigation state after simulating browser back operations")
    @Severity(SeverityLevel.NORMAL)
    @Story("Browser Navigation")
    public void testBrowserBackButtonSimulation() {
        // Navigate to product details
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName("Sauce Labs Backpack");
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Should be on product details");
        
        // Use browser back functionality (simulated by explicit navigation)
        InventoryPage backToInventory = productDetailsPage.goBackToProducts();
        assertTrue(backToInventory.isInventoryPageLoaded(), "Should be back on inventory page");
        
        // Navigate to cart
        backToInventory.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = backToInventory.clickShoppingCart();
        assertTrue(cartPage.isCartPageLoaded(), "Should be on cart page");
        
        // Use continue shopping (simulated back to inventory)
        InventoryPage backAgain = cartPage.continueShopping();
        assertTrue(backAgain.isInventoryPageLoaded(), "Should be back on inventory page");
        assertEquals(1, backAgain.getCartItemCount(), "Cart state should be preserved");
    }

    @Test
    @DisplayName("Navigation with cart state changes")
    @Description("Test navigation behavior when cart state changes during flow")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Stateful Navigation")
    public void testNavigationWithCartStateChanges() {
        // Add item and navigate to cart
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = inventoryPage.clickShoppingCart();
        assertEquals(1, cartPage.getCartItemCount(), "Cart should have 1 item");
        
        // Remove item from cart
        cartPage.removeItemFromCart("Sauce Labs Backpack");
        assertEquals(0, cartPage.getCartItemCount(), "Cart should be empty");
        
        // Navigate back to inventory
        InventoryPage returnedInventoryPage = cartPage.continueShopping();
        assertEquals(0, returnedInventoryPage.getCartItemCount(), "Cart should still be empty on inventory");
        
        // Add different item
        returnedInventoryPage.addProductToCart("Sauce Labs Bike Light");
        assertEquals(1, returnedInventoryPage.getCartItemCount(), "Cart should have new item");
        
        // Navigate to product details of different product
        ProductDetailsPage productDetailsPage = returnedInventoryPage.clickProductName("Sauce Labs Bolt T-Shirt");
        assertEquals(1, productDetailsPage.getCartItemCount(), "Cart state should be preserved");
        
        // Add another item from details page
        productDetailsPage.addToCart();
        assertEquals(2, productDetailsPage.getCartItemCount(), "Cart should have 2 items");
        
        // Navigate to cart and verify both items
        CartPage finalCartPage = productDetailsPage.clickShoppingCart();
        assertEquals(2, finalCartPage.getCartItemCount(), "Cart should have 2 items");
        assertTrue(finalCartPage.isItemInCart("Sauce Labs Bike Light"), "Should have bike light");
        assertTrue(finalCartPage.isItemInCart("Sauce Labs Bolt T-Shirt"), "Should have t-shirt");
    }

    @Test
    @DisplayName("Navigation accessibility and responsiveness")
    @Description("Test that navigation elements are accessible and responsive")
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation Accessibility")
    public void testNavigationAccessibilityAndResponsiveness() {
        // Test menu accessibility
        inventoryPage.openMenu();
        // Menu should open without errors (verified by not throwing exceptions)
        
        inventoryPage.closeMenu();
        // Menu should close without errors
        
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Page should remain functional after menu operations");
        
        // Test cart icon accessibility
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = inventoryPage.clickShoppingCart();
        assertTrue(cartPage.isCartPageLoaded(), "Cart should be accessible");
        
        // Test navigation elements are interactive
        InventoryPage backToInventory = cartPage.continueShopping();
        assertTrue(backToInventory.isInventoryPageLoaded(), "Continue shopping should work");
        
        // Test product navigation is accessible
        ProductDetailsPage productDetailsPage = backToInventory.clickProductName("Sauce Labs Backpack");
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Product details should be accessible");
        
        InventoryPage finalInventory = productDetailsPage.goBackToProducts();
        assertTrue(finalInventory.isInventoryPageLoaded(), "Back to products should work");
    }

    @Test
    @DisplayName("Cross-page navigation performance")
    @Description("Test that navigation between pages performs efficiently")
    @Severity(SeverityLevel.MINOR)
    @Story("Navigation Performance")
    public void testCrossPageNavigationPerformance() {
        long totalStartTime = System.currentTimeMillis();
        
        // Navigate through multiple pages and measure time
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName("Sauce Labs Backpack");
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Should navigate to details");
        
        CartPage cartPage = productDetailsPage.clickShoppingCart();
        assertTrue(cartPage.isCartPageLoaded(), "Should navigate to cart");
        
        InventoryPage backToInventory = cartPage.continueShopping();
        assertTrue(backToInventory.isInventoryPageLoaded(), "Should navigate back to inventory");
        
        long totalEndTime = System.currentTimeMillis();
        long totalTime = totalEndTime - totalStartTime;
        
        // Basic performance check - complete navigation flow should be reasonable
        assertTrue(totalTime < 10000, "Complete navigation flow should complete within 10 seconds, but took " + totalTime + "ms");
    }

    @Test
    @DisplayName("Navigation error handling")
    @Description("Test navigation behavior in edge cases")
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation Error Handling")
    public void testNavigationErrorHandling() {
        // Test navigation with empty cart
        assertEquals(0, inventoryPage.getCartItemCount(), "Cart should be empty initially");
        
        CartPage emptyCartPage = inventoryPage.clickShoppingCart();
        assertTrue(emptyCartPage.isCartPageLoaded(), "Should navigate to cart even when empty");
        assertTrue(emptyCartPage.isCartEmpty(), "Cart should be empty");
        
        // Test checkout with empty cart
        CheckoutPage checkoutPage = emptyCartPage.proceedToCheckout();
        assertTrue(checkoutPage.isCheckoutInformationPageLoaded(), "Should navigate to checkout even with empty cart");
        
        // Test navigation back to inventory from empty checkout
        CartPage backToCart = checkoutPage.clickCancel();
        assertTrue(backToCart.isCartPageLoaded(), "Should be able to navigate back to cart");
        
        InventoryPage backToInventory = backToCart.continueShopping();
        assertTrue(backToInventory.isInventoryPageLoaded(), "Should be able to navigate back to inventory");
    }
}
