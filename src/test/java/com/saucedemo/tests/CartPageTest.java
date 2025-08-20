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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Sauce Demo E-commerce")
@Feature("Shopping Cart Management")
@Owner("QA Team")
public class CartPageTest extends BaseTest {

    private CartPage cartPage;
    private InventoryPage inventoryPage;

    @BeforeEach
    void setupCartTests() {
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login("standard_user", "secret_sauce");
        
        // Add a product and navigate to cart for most tests
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        cartPage = inventoryPage.clickShoppingCart();
    }

    @Test
    @DisplayName("Verify cart page loads correctly")
    @Description("Test that cart page loads with correct elements and navigation")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Cart Page Display")
    public void testCartPageLoading() {
        assertTrue(cartPage.isCartPageLoaded(), "Cart page should be loaded");
        assertEquals("Your Cart", cartPage.getCartHeaderText(), "Cart header should be correct");
        assertEquals(1, cartPage.getCartItemCount(), "Cart should have 1 item");
    }

    @Test
    @DisplayName("Verify cart displays item information correctly")
    @Description("Test that cart shows complete item information including name, price, and description")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Item Display")
    public void testCartItemInformationDisplay() {
        List<String> itemNames = cartPage.getCartItemNames();
        List<String> itemPrices = cartPage.getCartItemPrices();
        
        assertEquals(1, itemNames.size(), "Should have 1 item name");
        assertEquals(1, itemPrices.size(), "Should have 1 item price");
        assertTrue(itemNames.contains("Sauce Labs Backpack"), "Should contain backpack");
        
        String itemPrice = cartPage.getItemPrice("Sauce Labs Backpack");
        assertTrue(itemPrice.startsWith("$"), "Price should start with $");
        assertTrue(itemPrice.matches("\\$\\d+\\.\\d{2}"), "Price should be in format $XX.XX");
        
        String itemDescription = cartPage.getItemDescription("Sauce Labs Backpack");
        assertFalse(itemDescription.isEmpty(), "Item description should not be empty");
    }

    @Test
    @DisplayName("Verify item quantity display")
    @Description("Test that item quantities are correctly displayed in cart")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Item Management")
    public void testItemQuantityDisplay() {
        int quantity = cartPage.getItemQuantity("Sauce Labs Backpack");
        assertEquals(1, quantity, "Item quantity should be 1");
    }

    @Test
    @DisplayName("Remove single item from cart")
    @Description("Test removing an individual item from the cart")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Item Management")
    public void testRemoveSingleItemFromCart() {
        assertTrue(cartPage.isItemInCart("Sauce Labs Backpack"), "Item should be in cart initially");
        assertEquals(1, cartPage.getCartItemCount(), "Cart should have 1 item initially");
        
        cartPage.removeItemFromCart("Sauce Labs Backpack");
        
        assertFalse(cartPage.isItemInCart("Sauce Labs Backpack"), "Item should be removed from cart");
        assertEquals(0, cartPage.getCartItemCount(), "Cart should be empty after removal");
        assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
    }

    @Test
    @DisplayName("Remove multiple items from cart")
    @Description("Test removing multiple items from cart individually")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Item Management")
    public void testRemoveMultipleItemsFromCart() {
        // Add more items first
        InventoryPage returnedInventoryPage = cartPage.continueShopping();
        returnedInventoryPage.addProductToCart("Sauce Labs Bike Light");
        returnedInventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        CartPage updatedCartPage = returnedInventoryPage.clickShoppingCart();
        
        assertEquals(3, updatedCartPage.getCartItemCount(), "Cart should have 3 items");
        
        // Remove items one by one
        updatedCartPage.removeItemFromCart("Sauce Labs Bike Light");
        assertEquals(2, updatedCartPage.getCartItemCount(), "Cart should have 2 items after first removal");
        
        updatedCartPage.removeItemFromCart("Sauce Labs Backpack");
        assertEquals(1, updatedCartPage.getCartItemCount(), "Cart should have 1 item after second removal");
        
        updatedCartPage.removeItemFromCart("Sauce Labs Bolt T-Shirt");
        assertEquals(0, updatedCartPage.getCartItemCount(), "Cart should be empty after removing all items");
        assertTrue(updatedCartPage.isCartEmpty(), "Cart should be empty");
    }

    @Test
    @DisplayName("Remove all items from cart using helper method")
    @Description("Test removing all items from cart using the remove all functionality")
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart Item Management")
    public void testRemoveAllItemsFromCart() {
        // Add more items first
        InventoryPage returnedInventoryPage = cartPage.continueShopping();
        returnedInventoryPage.addProductToCart("Sauce Labs Bike Light");
        returnedInventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        CartPage updatedCartPage = returnedInventoryPage.clickShoppingCart();
        
        assertEquals(3, updatedCartPage.getCartItemCount(), "Cart should have 3 items");
        
        updatedCartPage.removeAllItems();
        
        assertEquals(0, updatedCartPage.getCartItemCount(), "Cart should be empty after removing all items");
        assertTrue(updatedCartPage.isCartEmpty(), "Cart should be empty");
    }

    @ParameterizedTest
    @DisplayName("Verify cart item details for different products")
    @Description("Test that different products display correct information in cart")
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart Item Display")
    @ValueSource(strings = {
        "Sauce Labs Bike Light",
        "Sauce Labs Bolt T-Shirt", 
        "Sauce Labs Fleece Jacket",
        "Sauce Labs Onesie",
        "Test.allTheThings() T-Shirt (Red)"
    })
    public void testCartItemDetailsForDifferentProducts(String productName) {
        // Start with empty cart
        cartPage.removeAllItems();
        assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
        
        // Add specific product
        InventoryPage returnedInventoryPage = cartPage.continueShopping();
        String expectedPrice = returnedInventoryPage.getProductPrice(productName);
        String expectedDescription = returnedInventoryPage.getProductDescription(productName);
        returnedInventoryPage.addProductToCart(productName);
        CartPage updatedCartPage = returnedInventoryPage.clickShoppingCart();
        
        // Verify product details in cart
        assertTrue(updatedCartPage.isItemInCart(productName), "Product should be in cart");
        assertEquals(expectedPrice, updatedCartPage.getItemPrice(productName), "Price should match");
        assertEquals(expectedDescription, updatedCartPage.getItemDescription(productName), "Description should match");
        assertEquals(1, updatedCartPage.getItemQuantity(productName), "Quantity should be 1");
    }

    @Test
    @DisplayName("Continue shopping navigation")
    @Description("Test continue shopping button returns to inventory page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Navigation")
    public void testContinueShoppingNavigation() {
        InventoryPage returnedInventoryPage = cartPage.continueShopping();
        
        assertTrue(returnedInventoryPage.isInventoryPageLoaded(), "Should return to inventory page");
        assertEquals(6, returnedInventoryPage.getProductCount(), "Should display all products");
        assertEquals(1, returnedInventoryPage.getCartItemCount(), "Cart count should be preserved");
    }

    @Test
    @DisplayName("Proceed to checkout navigation")
    @Description("Test checkout button navigates to checkout page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Navigation")
    public void testProceedToCheckoutNavigation() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        
        assertTrue(checkoutPage.isCheckoutInformationPageLoaded(), "Should navigate to checkout page");
        assertEquals("Checkout: Your Information", checkoutPage.getCheckoutTitle(), "Checkout title should be correct");
    }

    @Test
    @DisplayName("Navigate to product details from cart")
    @Description("Test clicking item name navigates to product details page")
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart Navigation")
    public void testNavigateToProductDetailsFromCart() {
        ProductDetailsPage productDetailsPage = cartPage.clickItemName("Sauce Labs Backpack");
        
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Should navigate to product details page");
        assertEquals("Sauce Labs Backpack", productDetailsPage.getProductName(), "Product name should match");
        assertTrue(productDetailsPage.isProductImageDisplayed(), "Product image should be displayed");
    }

    @Test
    @DisplayName("Shopping cart badge consistency")
    @Description("Test that shopping cart badge shows correct count")
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart Badge Display")
    public void testShoppingCartBadgeConsistency() {
        assertEquals(1, cartPage.getShoppingCartBadgeCount(), "Cart badge should show 1");
        
        // Add another item
        InventoryPage returnedInventoryPage = cartPage.continueShopping();
        returnedInventoryPage.addProductToCart("Sauce Labs Bike Light");
        CartPage updatedCartPage = returnedInventoryPage.clickShoppingCart();
        
        assertEquals(2, updatedCartPage.getShoppingCartBadgeCount(), "Cart badge should show 2");
        assertEquals(2, updatedCartPage.getCartItemCount(), "Cart item count should match badge");
    }

    @Test
    @DisplayName("Empty cart state verification")
    @Description("Test cart behavior when empty")
    @Severity(SeverityLevel.NORMAL)
    @Story("Empty Cart Handling")
    public void testEmptyCartState() {
        cartPage.removeAllItems();
        
        assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
        assertEquals(0, cartPage.getCartItemCount(), "Cart count should be 0");
        assertEquals(0, cartPage.getShoppingCartBadgeCount(), "Cart badge should show 0");
        
        List<String> itemNames = cartPage.getCartItemNames();
        assertTrue(itemNames.isEmpty(), "Item names list should be empty");
        
        List<String> itemPrices = cartPage.getCartItemPrices();
        assertTrue(itemPrices.isEmpty(), "Item prices list should be empty");
    }

    @Test
    @DisplayName("Checkout with empty cart")
    @Description("Test attempting to checkout with empty cart")
    @Severity(SeverityLevel.NORMAL)
    @Story("Empty Cart Handling")
    public void testCheckoutWithEmptyCart() {
        cartPage.removeAllItems();
        assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
        
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        
        // Should still navigate to checkout page (application behavior)
        assertTrue(checkoutPage.isCheckoutInformationPageLoaded(), "Should navigate to checkout even with empty cart");
    }

    @Test
    @DisplayName("Cart state persistence during navigation")
    @Description("Test that cart contents are preserved during navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart State Management")
    public void testCartStatePersistenceDuringNavigation() {
        String originalProductName = "Sauce Labs Backpack";
        assertTrue(cartPage.isItemInCart(originalProductName), "Original item should be in cart");
        
        // Navigate to inventory and back
        InventoryPage inventoryPage = cartPage.continueShopping();
        CartPage returnedCartPage = inventoryPage.clickShoppingCart();
        
        assertTrue(returnedCartPage.isItemInCart(originalProductName), "Item should still be in cart after navigation");
        assertEquals(1, returnedCartPage.getCartItemCount(), "Cart count should be preserved");
        
        // Navigate to product details and back
        ProductDetailsPage productDetailsPage = returnedCartPage.clickItemName(originalProductName);
        CartPage finalCartPage = productDetailsPage.clickShoppingCart();
        
        assertTrue(finalCartPage.isItemInCart(originalProductName), "Item should still be in cart after product details navigation");
        assertEquals(1, finalCartPage.getCartItemCount(), "Cart count should still be preserved");
    }

    @Test
    @DisplayName("Price formatting validation in cart")
    @Description("Test that all prices in cart are properly formatted")
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart Display Validation")
    public void testPriceFormattingValidation() {
        // Add multiple items to test various prices
        InventoryPage returnedInventoryPage = cartPage.continueShopping();
        returnedInventoryPage.addProductToCart("Sauce Labs Bike Light");
        returnedInventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        CartPage updatedCartPage = returnedInventoryPage.clickShoppingCart();
        
        List<String> itemPrices = updatedCartPage.getCartItemPrices();
        
        for (String price : itemPrices) {
            assertTrue(price.startsWith("$"), "Price should start with $: " + price);
            assertTrue(price.matches("\\$\\d+\\.\\d{2}"), "Price should be in format $XX.XX: " + price);
        }
    }
}
