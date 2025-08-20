package com.saucedemo.tests;

import com.saucedemo.pages.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Sauce Demo E-commerce")
@Feature("End-to-End Shopping Flow")
@Owner("QA Team")
public class E2ETest extends BaseTest {

    @Test
    @DisplayName("Complete purchase flow - single item")
    @Description("Test complete end-to-end purchase flow with a single item")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Complete Purchase Flow")
    public void testCompleteSingleItemPurchase() {
        // Login
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Should be on inventory page");

        // Add product to cart
        String productName = "Sauce Labs Backpack";
        String productPrice = inventoryPage.getProductPrice(productName);
        inventoryPage.addProductToCart(productName);
        assertEquals(1, inventoryPage.getCartItemCount(), "Cart should have 1 item");

        // Go to cart
        CartPage cartPage = inventoryPage.clickShoppingCart();
        assertTrue(cartPage.isCartPageLoaded(), "Should be on cart page");
        assertEquals(1, cartPage.getCartItemCount(), "Cart should have 1 item");
        assertTrue(cartPage.isItemInCart(productName), "Product should be in cart");
        assertEquals(productPrice, cartPage.getItemPrice(productName), "Price should match");

        // Proceed to checkout
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        assertTrue(checkoutPage.isCheckoutInformationPageLoaded(), "Should be on checkout information page");

        // Fill checkout information
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), "Should be on checkout overview page");

        // Verify order summary
        List<String> checkoutItems = checkoutPage.getCheckoutItemNames();
        assertTrue(checkoutItems.contains(productName), "Product should be in checkout summary");
        assertEquals(productPrice, checkoutPage.getItemPrice(productName), "Price should match in summary");

        // Complete purchase
        checkoutPage.clickFinish();
        assertTrue(checkoutPage.isCheckoutCompletePageLoaded(), "Should be on checkout complete page");
        assertEquals("Thank you for your order!", checkoutPage.getCompleteHeader(), 
                "Completion header should be correct");

        // Return to inventory
        InventoryPage finalInventoryPage = checkoutPage.clickBackHome();
        assertTrue(finalInventoryPage.isInventoryPageLoaded(), "Should return to inventory page");
    }

    @Test
    @DisplayName("Complete purchase flow - multiple items")
    @Description("Test complete end-to-end purchase flow with multiple items")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Complete Purchase Flow")
    public void testCompleteMultipleItemsPurchase() {
        // Login
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");

        // Add multiple products to cart
        String[] products = {"Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt"};
        for (String product : products) {
            inventoryPage.addProductToCart(product);
        }
        assertEquals(3, inventoryPage.getCartItemCount(), "Cart should have 3 items");

        // Go to cart and verify all items
        CartPage cartPage = inventoryPage.clickShoppingCart();
        assertEquals(3, cartPage.getCartItemCount(), "Cart should have 3 items");
        
        for (String product : products) {
            assertTrue(cartPage.isItemInCart(product), "Product should be in cart: " + product);
        }

        // Complete checkout
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.completeCheckout("Jane", "Smith", "67890");

        assertTrue(checkoutPage.isCheckoutCompletePageLoaded(), "Should complete checkout successfully");
        assertEquals("Thank you for your order!", checkoutPage.getCompleteHeader(), 
                "Order should be completed successfully");
    }

    @ParameterizedTest
    @DisplayName("Checkout with different user information")
    @Description("Test checkout process with various user information combinations")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Process")
    @CsvSource({
        "Alice, Johnson, 12345",
        "Bob, Williams, 67890",
        "Charlie, Brown, 54321",
        "Diana, Davis, 98765"
    })
    public void testCheckoutWithDifferentUserInfo(String firstName, String lastName, String postalCode) {
        // Login and add product
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart("Sauce Labs Backpack");

        // Complete checkout with provided information
        CartPage cartPage = inventoryPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.completeCheckout(firstName, lastName, postalCode);

        assertTrue(checkoutPage.isCheckoutCompletePageLoaded(), 
                "Checkout should complete with user info: " + firstName + " " + lastName);
    }

    @Test
    @DisplayName("Shopping cart management throughout flow")
    @Description("Test adding/removing items during shopping flow")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Shopping Cart Management")
    public void testShoppingCartManagement() {
        // Login
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");

        // Add multiple items
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        assertEquals(3, inventoryPage.getCartItemCount(), "Should have 3 items");

        // Go to cart and remove one item
        CartPage cartPage = inventoryPage.clickShoppingCart();
        cartPage.removeItemFromCart("Sauce Labs Bike Light");
        assertEquals(2, cartPage.getCartItemCount(), "Should have 2 items after removal");

        // Continue shopping and add another item
        InventoryPage returnedInventoryPage = cartPage.continueShopping();
        returnedInventoryPage.addProductToCart("Sauce Labs Fleece Jacket");
        assertEquals(3, returnedInventoryPage.getCartItemCount(), "Should have 3 items again");

        // Verify final cart contents
        CartPage finalCartPage = returnedInventoryPage.clickShoppingCart();
        assertTrue(finalCartPage.isItemInCart("Sauce Labs Backpack"), "Backpack should be in cart");
        assertTrue(finalCartPage.isItemInCart("Sauce Labs Bolt T-Shirt"), "T-shirt should be in cart");
        assertTrue(finalCartPage.isItemInCart("Sauce Labs Fleece Jacket"), "Jacket should be in cart");
        assertFalse(finalCartPage.isItemInCart("Sauce Labs Bike Light"), "Bike light should not be in cart");
    }

    @Test
    @DisplayName("Product details to purchase flow")
    @Description("Test purchasing workflow starting from product details page")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Details Flow")
    public void testProductDetailsToPurchaseFlow() {
        // Login and navigate to product details
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        
        String productName = "Sauce Labs Backpack";
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName(productName);
        
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Should be on product details page");
        assertTrue(productDetailsPage.isProductImageDisplayed(), "Product image should be displayed");

        // Add to cart from details page
        productDetailsPage.addToCart();
        assertTrue(productDetailsPage.isProductAddedToCart(), "Product should be added to cart");
        assertEquals(1, productDetailsPage.getCartItemCount(), "Cart should have 1 item");

        // Go to cart and complete purchase
        CartPage cartPage = productDetailsPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.completeCheckout("Test", "User", "12345");

        assertTrue(checkoutPage.isCheckoutCompletePageLoaded(), "Purchase should complete successfully");
    }

    @Test
    @DisplayName("Checkout validation - missing information")
    @Description("Test checkout form validation with missing required fields")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Validation")
    public void testCheckoutValidationMissingInfo() {
        // Login and add product
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart("Sauce Labs Backpack");

        CartPage cartPage = inventoryPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        // Try to continue without filling required fields
        checkoutPage.clickContinue();
        
        assertTrue(checkoutPage.isErrorMessageDisplayed(), "Error message should be displayed");
        String errorMessage = checkoutPage.getErrorMessage();
        assertTrue(errorMessage.contains("First Name is required"), 
                "Error should indicate first name is required");

        // Fill first name only and try again
        checkoutPage.clearErrorMessage();
        checkoutPage.enterFirstName("John").clickContinue();
        
        assertTrue(checkoutPage.isErrorMessageDisplayed(), "Error message should be displayed");
        errorMessage = checkoutPage.getErrorMessage();
        assertTrue(errorMessage.contains("Last Name is required"), 
                "Error should indicate last name is required");

        // Fill first and last name, try again
        checkoutPage.clearErrorMessage();
        checkoutPage.enterLastName("Doe").clickContinue();
        
        assertTrue(checkoutPage.isErrorMessageDisplayed(), "Error message should be displayed");
        errorMessage = checkoutPage.getErrorMessage();
        assertTrue(errorMessage.contains("Postal Code is required"), 
                "Error should indicate postal code is required");
    }

    @Test
    @DisplayName("Cancel checkout process")
    @Description("Test canceling checkout at various stages")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Process")
    public void testCancelCheckoutProcess() {
        // Login and add product
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart("Sauce Labs Backpack");

        // Go to cart and proceed to checkout
        CartPage cartPage = inventoryPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        assertTrue(checkoutPage.isCheckoutInformationPageLoaded(), "Should be on checkout page");

        // Cancel checkout and return to cart
        CartPage returnedCartPage = checkoutPage.clickCancel();
        assertTrue(returnedCartPage.isCartPageLoaded(), "Should return to cart page");
        assertEquals(1, returnedCartPage.getCartItemCount(), "Cart should still have the item");
    }

    @Test
    @DisplayName("Price verification throughout purchase flow")
    @Description("Test that prices remain consistent throughout the entire purchase flow")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Price Consistency")
    public void testPriceConsistencyThroughoutFlow() {
        // Login
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");

        // Get price from inventory page
        String productName = "Sauce Labs Backpack";
        String inventoryPrice = inventoryPage.getProductPrice(productName);

        // Get price from product details page
        ProductDetailsPage productDetailsPage = inventoryPage.clickProductName(productName);
        String detailsPrice = productDetailsPage.getProductPrice();
        assertEquals(inventoryPrice, detailsPrice, "Price should match between inventory and details");

        // Add to cart and check price
        productDetailsPage.addToCart();
        CartPage cartPage = productDetailsPage.clickShoppingCart();
        String cartPrice = cartPage.getItemPrice(productName);
        assertEquals(inventoryPrice, cartPrice, "Price should match in cart");

        // Check price in checkout summary
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        String checkoutPrice = checkoutPage.getItemPrice(productName);
        assertEquals(inventoryPrice, checkoutPrice, "Price should match in checkout summary");

        // Verify total calculations
        String itemTotal = checkoutPage.getItemTotal();
        String tax = checkoutPage.getTax();
        String total = checkoutPage.getTotal();
        
        assertFalse(itemTotal.isEmpty(), "Item total should be displayed");
        assertFalse(tax.isEmpty(), "Tax should be displayed");
        assertFalse(total.isEmpty(), "Total should be displayed");
    }
}
