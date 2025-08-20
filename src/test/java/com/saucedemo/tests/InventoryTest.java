package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.ProductDetailsPage;
import com.saucedemo.pages.CartPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Sauce Demo E-commerce")
@Feature("Inventory Management")
@Owner("QA Team")
public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginBeforeEach() {
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login("standard_user", "secret_sauce");
    }

    @Test
    @DisplayName("Verify inventory page loads with products")
    @Description("Test that inventory page loads correctly with all products displayed")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Product Display")
    public void testInventoryPageLoading() {
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page should be loaded");
        assertEquals("Swag Labs", inventoryPage.getAppLogoText(), "App logo should be correct");
        assertEquals(6, inventoryPage.getProductCount(), "Should display 6 products");
    }

    @Test
    @DisplayName("Verify all product names are displayed")
    @Description("Test that all expected product names are visible on inventory page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Display")
    public void testProductNamesDisplay() {
        List<String> productNames = inventoryPage.getProductNames();
        
        assertEquals(6, productNames.size(), "Should have 6 product names");
        assertTrue(productNames.contains("Sauce Labs Backpack"), "Should contain Sauce Labs Backpack");
        assertTrue(productNames.contains("Sauce Labs Bike Light"), "Should contain Sauce Labs Bike Light");
        assertTrue(productNames.contains("Sauce Labs Bolt T-Shirt"), "Should contain Sauce Labs Bolt T-Shirt");
        assertTrue(productNames.contains("Sauce Labs Fleece Jacket"), "Should contain Sauce Labs Fleece Jacket");
        assertTrue(productNames.contains("Sauce Labs Onesie"), "Should contain Sauce Labs Onesie");
        assertTrue(productNames.contains("Test.allTheThings() T-Shirt (Red)"), "Should contain Test.allTheThings() T-Shirt");
    }

    @Test
    @DisplayName("Verify all product prices are displayed")
    @Description("Test that all product prices are visible and properly formatted")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Display")
    public void testProductPricesDisplay() {
        List<String> productPrices = inventoryPage.getProductPrices();
        
        assertEquals(6, productPrices.size(), "Should have 6 product prices");
        
        // Verify all prices start with $ and contain valid price format
        for (String price : productPrices) {
            assertTrue(price.startsWith("$"), "Price should start with $: " + price);
            assertTrue(price.matches("\\$\\d+\\.\\d{2}"), "Price should be in format $XX.XX: " + price);
        }
    }

    @ParameterizedTest
    @DisplayName("Add individual products to cart")
    @Description("Test adding each product to cart individually")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Shopping Cart")
    @ValueSource(strings = {
        "Sauce Labs Backpack",
        "Sauce Labs Bike Light", 
        "Sauce Labs Bolt T-Shirt",
        "Sauce Labs Fleece Jacket",
        "Sauce Labs Onesie",
        "Test.allTheThings() T-Shirt (Red)"
    })
    public void testAddSingleProductToCart(String productName) {
        assertEquals(0, inventoryPage.getCartItemCount(), "Cart should be empty initially");
        
        inventoryPage.addProductToCart(productName);
        
        assertEquals(1, inventoryPage.getCartItemCount(), "Cart should have 1 item");
        assertTrue(inventoryPage.isProductInCart(productName), "Product should be marked as added to cart");
        
        // Remove product to reset state for next test
        inventoryPage.removeProductFromCart(productName);
        assertEquals(0, inventoryPage.getCartItemCount(), "Cart should be empty after removal");
    }

    @Test
    @DisplayName("Add multiple products to cart")
    @Description("Test adding multiple products to cart and verify cart count")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Shopping Cart")
    public void testAddMultipleProductsToCart() {
        String[] products = {"Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt"};
        
        for (int i = 0; i < products.length; i++) {
            inventoryPage.addProductToCart(products[i]);
            assertEquals(i + 1, inventoryPage.getCartItemCount(), 
                    "Cart should have " + (i + 1) + " items after adding " + products[i]);
        }
        
        // Verify all products are marked as added
        for (String product : products) {
            assertTrue(inventoryPage.isProductInCart(product), 
                    "Product should be marked as added: " + product);
        }
    }

    @Test
    @DisplayName("Remove products from cart")
    @Description("Test removing products from cart using remove buttons")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Shopping Cart")
    public void testRemoveProductsFromCart() {
        String[] products = {"Sauce Labs Backpack", "Sauce Labs Bike Light"};
        
        // Add products first
        for (String product : products) {
            inventoryPage.addProductToCart(product);
        }
        assertEquals(2, inventoryPage.getCartItemCount(), "Cart should have 2 items");
        
        // Remove first product
        inventoryPage.removeProductFromCart(products[0]);
        assertEquals(1, inventoryPage.getCartItemCount(), "Cart should have 1 item after removal");
        assertFalse(inventoryPage.isProductInCart(products[0]), 
                "First product should not be in cart");
        assertTrue(inventoryPage.isProductInCart(products[1]), 
                "Second product should still be in cart");
        
        // Remove second product
        inventoryPage.removeProductFromCart(products[1]);
        assertEquals(0, inventoryPage.getCartItemCount(), "Cart should be empty");
        assertFalse(inventoryPage.isProductInCart(products[1]), 
                "Second product should not be in cart");
    }

    @Test
    @DisplayName("Product sorting functionality")
    @Description("Test product sorting by name and price")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Display")
    public void testProductSorting() {
        // Test sorting by name A to Z (default)
        List<String> defaultOrder = inventoryPage.getProductNames();
        
        // Sort by name Z to A
        inventoryPage.sortProducts("za");
        List<String> reverseOrder = inventoryPage.getProductNames();
        assertNotEquals(defaultOrder, reverseOrder, "Order should change after sorting Z to A");
        
        // Sort by price low to high
        inventoryPage.sortProducts("lohi");
        List<String> priceOrderLowHigh = inventoryPage.getProductNames();
        assertNotEquals(reverseOrder, priceOrderLowHigh, "Order should change when sorting by price");
        
        // Sort by price high to low
        inventoryPage.sortProducts("hilo");
        List<String> priceOrderHighLow = inventoryPage.getProductNames();
        assertNotEquals(priceOrderLowHigh, priceOrderHighLow, "Order should change when sorting high to low");
    }

    @Test
    @DisplayName("Navigate to product details page")
    @Description("Test clicking on product name to view details")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Display")
    public void testNavigateToProductDetails() {
        String productName = "Sauce Labs Backpack";
        String expectedPrice = inventoryPage.getProductPrice(productName);
        String expectedDescription = inventoryPage.getProductDescription(productName);
        
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName(productName);
        
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), 
                "Product details page should be loaded");
        assertEquals(productName, productDetailsPage.getProductName(), 
                "Product name should match");
        assertEquals(expectedPrice, productDetailsPage.getProductPrice(), 
                "Product price should match");
        assertEquals(expectedDescription, productDetailsPage.getProductDescription(), 
                "Product description should match");
    }

    @Test
    @DisplayName("Navigate to shopping cart")
    @Description("Test clicking shopping cart icon to view cart")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Shopping Cart")
    public void testNavigateToShoppingCart() {
        // Add a product first
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        assertEquals(1, inventoryPage.getCartItemCount(), "Cart should have 1 item");
        
        CartPage cartPage = inventoryPage.clickShoppingCart();
        
        assertTrue(cartPage.isCartPageLoaded(), "Cart page should be loaded");
        assertEquals(1, cartPage.getCartItemCount(), "Cart should have 1 item");
        assertTrue(cartPage.isItemInCart("Sauce Labs Backpack"), 
                "Backpack should be in cart");
    }

    @Test
    @DisplayName("Menu functionality")
    @Description("Test opening and closing the hamburger menu")
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation")
    public void testMenuFunctionality() {
        inventoryPage.openMenu();
        // Wait a moment for menu to open
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        inventoryPage.closeMenu();
        // Menu should close without errors
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Should still be on inventory page");
    }

    @Test
    @DisplayName("Logout functionality")
    @Description("Test logging out from inventory page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigation")
    public void testLogoutFunctionality() {
        LoginPage loginPage = inventoryPage.logout();
        
        assertTrue(loginPage.isLoginFormDisplayed(), "Should return to login page");
        assertEquals("Swag Labs", loginPage.getLoginLogoText(), "Login logo should be displayed");
    }

    @Test
    @DisplayName("Reset app state functionality")
    @Description("Test reset app state clears cart")
    @Severity(SeverityLevel.NORMAL)
    @Story("Shopping Cart")
    public void testResetAppState() {
        // Add products to cart
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        assertEquals(2, inventoryPage.getCartItemCount(), "Cart should have 2 items");
        
        // Reset app state
        inventoryPage.resetAppState();
        
        // Cart should be cleared
        assertEquals(0, inventoryPage.getCartItemCount(), "Cart should be empty after reset");
    }

    @Test
    @DisplayName("Verify product information completeness")
    @Description("Test that all products have complete information")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Display")
    public void testProductInformationCompleteness() {
        List<String> productNames = inventoryPage.getProductNames();
        
        for (String productName : productNames) {
            String price = inventoryPage.getProductPrice(productName);
            String description = inventoryPage.getProductDescription(productName);
            
            assertFalse(price.isEmpty(), "Product price should not be empty for: " + productName);
            assertFalse(description.isEmpty(), "Product description should not be empty for: " + productName);
            assertTrue(price.startsWith("$"), "Price should start with $ for: " + productName);
        }
    }
}
