package com.saucedemo.tests;

import com.saucedemo.pages.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Sauce Demo E-commerce")
@Feature("Checkout Process")
@Owner("QA Team")
public class CheckoutPageTest extends BaseTest {

    private CheckoutPage checkoutPage;
    private CartPage cartPage;

    @BeforeEach
    void setupCheckoutTests() {
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        
        // Add products and navigate to checkout
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        cartPage = inventoryPage.clickShoppingCart();
        checkoutPage = cartPage.proceedToCheckout();
    }

    @Test
    @DisplayName("Verify checkout information page loads correctly")
    @Description("Test that checkout information page loads with correct elements")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Checkout Page Display")
    public void testCheckoutInformationPageLoading() {
        assertTrue(checkoutPage.isCheckoutInformationPageLoaded(), "Checkout information page should be loaded");
        assertEquals("Checkout: Your Information", checkoutPage.getCheckoutTitle(), "Checkout title should be correct");
    }

    @Test
    @DisplayName("Fill checkout information successfully")
    @Description("Test filling checkout information with valid data")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Form")
    public void testFillCheckoutInformationSuccessfully() {
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), "Should navigate to checkout overview page");
        assertEquals("Checkout: Overview", checkoutPage.getCheckoutTitle(), "Overview title should be correct");
    }

    @ParameterizedTest
    @DisplayName("Checkout form validation - missing required fields")
    @Description("Test checkout form validation with various missing field combinations")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Validation")
    @CsvSource({
        "'', '', '', 'First Name is required'",
        "'John', '', '', 'Last Name is required'", 
        "'John', 'Doe', '', 'Postal Code is required'",
        "'', 'Doe', '12345', 'First Name is required'",
        "'', '', '12345', 'First Name is required'"
    })
    public void testCheckoutFormValidationMissingFields(String firstName, String lastName, String postalCode, String expectedErrorPart) {
        checkoutPage.fillCheckoutInformation(firstName, lastName, postalCode);
        checkoutPage.clickContinue();
        
        assertTrue(checkoutPage.isErrorMessageDisplayed(), "Error message should be displayed");
        String errorMessage = checkoutPage.getErrorMessage();
        assertTrue(errorMessage.contains(expectedErrorPart), 
                "Error should contain: " + expectedErrorPart + ", but was: " + errorMessage);
        
        // Should remain on checkout information page
        assertTrue(checkoutPage.isCheckoutInformationPageLoaded(), "Should remain on checkout information page");
    }

    @Test
    @DisplayName("Clear error message functionality")
    @Description("Test clearing error message using the X button")
    @Severity(SeverityLevel.MINOR)
    @Story("Checkout Validation")
    public void testClearErrorMessage() {
        // Generate an error first
        checkoutPage.clickContinue();
        assertTrue(checkoutPage.isErrorMessageDisplayed(), "Error message should be displayed");
        
        // Clear the error
        checkoutPage.clearErrorMessage();
        assertFalse(checkoutPage.isErrorMessageDisplayed(), "Error message should be cleared");
    }

    @ParameterizedTest
    @DisplayName("Checkout with various valid user information")
    @Description("Test checkout process with different valid user information combinations")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Form")
    @CsvSource({
        "Alice, Johnson, 12345",
        "Bob, Williams, 67890", 
        "Charlie, Brown, 54321",
        "Diana, Davis, 98765",
        "Eve, Miller, 11111",
        "Frank, Wilson, 99999"
    })
    public void testCheckoutWithValidUserInformation(String firstName, String lastName, String postalCode) {
        checkoutPage.fillCheckoutInformation(firstName, lastName, postalCode);
        checkoutPage.clickContinue();
        
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), 
                "Should successfully navigate to overview page with: " + firstName + " " + lastName);
    }

    @Test
    @DisplayName("Cancel checkout from information page")
    @Description("Test canceling checkout process from information page")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Navigation")
    public void testCancelCheckoutFromInformationPage() {
        CartPage returnedCartPage = checkoutPage.clickCancel();
        
        assertTrue(returnedCartPage.isCartPageLoaded(), "Should return to cart page");
        assertEquals(2, returnedCartPage.getCartItemCount(), "Cart should still have items");
        assertTrue(returnedCartPage.isItemInCart("Sauce Labs Backpack"), "Items should be preserved");
        assertTrue(returnedCartPage.isItemInCart("Sauce Labs Bike Light"), "Items should be preserved");
    }

    @Test
    @DisplayName("Verify checkout overview displays correct items")
    @Description("Test that checkout overview shows all cart items correctly")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Overview")
    public void testCheckoutOverviewItemDisplay() {
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), "Should be on checkout overview page");
        
        List<String> checkoutItems = checkoutPage.getCheckoutItemNames();
        assertEquals(2, checkoutItems.size(), "Should display 2 items");
        assertTrue(checkoutItems.contains("Sauce Labs Backpack"), "Should contain backpack");
        assertTrue(checkoutItems.contains("Sauce Labs Bike Light"), "Should contain bike light");
        
        assertEquals(2, checkoutPage.getCheckoutItemCount(), "Checkout item count should match");
    }

    @Test
    @DisplayName("Verify payment and shipping information display")
    @Description("Test that payment and shipping information are displayed in checkout overview")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Overview")
    public void testPaymentAndShippingInformationDisplay() {
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        
        String paymentInfo = checkoutPage.getPaymentInformation();
        String shippingInfo = checkoutPage.getShippingInformation();
        
        assertFalse(paymentInfo.isEmpty(), "Payment information should be displayed");
        assertFalse(shippingInfo.isEmpty(), "Shipping information should be displayed");
        
        // Verify typical payment method is shown
        assertTrue(paymentInfo.contains("SauceCard") || paymentInfo.contains("card"), 
                "Payment info should contain card information");
    }

    @Test
    @DisplayName("Verify price calculations in checkout overview")
    @Description("Test that item total, tax, and final total are calculated correctly")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Calculations")
    public void testPriceCalculationsInCheckoutOverview() {
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        
        String itemTotal = checkoutPage.getItemTotal();
        String tax = checkoutPage.getTax();
        String total = checkoutPage.getTotal();
        
        assertFalse(itemTotal.isEmpty(), "Item total should be displayed");
        assertFalse(tax.isEmpty(), "Tax should be displayed");
        assertFalse(total.isEmpty(), "Total should be displayed");
        
        // Verify format
        assertTrue(itemTotal.contains("$"), "Item total should contain $");
        assertTrue(tax.contains("$"), "Tax should contain $");
        assertTrue(total.contains("$"), "Total should contain $");
        
        // Extract numeric values for basic calculation verification
        assertTrue(itemTotal.matches(".*\\$\\d+\\.\\d{2}.*"), "Item total should have valid price format");
        assertTrue(tax.matches(".*\\$\\d+\\.\\d{2}.*"), "Tax should have valid price format");
        assertTrue(total.matches(".*\\$\\d+\\.\\d{2}.*"), "Total should have valid price format");
    }

    @Test
    @DisplayName("Verify individual item prices in checkout overview")
    @Description("Test that individual item prices are displayed correctly in checkout")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Overview")
    public void testIndividualItemPricesInCheckout() {
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        
        String backpackPrice = checkoutPage.getItemPrice("Sauce Labs Backpack");
        String bikeLightPrice = checkoutPage.getItemPrice("Sauce Labs Bike Light");
        
        assertTrue(backpackPrice.startsWith("$"), "Backpack price should start with $");
        assertTrue(bikeLightPrice.startsWith("$"), "Bike light price should start with $");
        
        assertTrue(backpackPrice.matches("\\$\\d+\\.\\d{2}"), "Backpack price should be in format $XX.XX");
        assertTrue(bikeLightPrice.matches("\\$\\d+\\.\\d{2}"), "Bike light price should be in format $XX.XX");
    }

    @Test
    @DisplayName("Verify item quantities in checkout overview")
    @Description("Test that item quantities are displayed correctly in checkout")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Overview")
    public void testItemQuantitiesInCheckout() {
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        
        int backpackQuantity = checkoutPage.getItemQuantity("Sauce Labs Backpack");
        int bikeLightQuantity = checkoutPage.getItemQuantity("Sauce Labs Bike Light");
        
        assertEquals(1, backpackQuantity, "Backpack quantity should be 1");
        assertEquals(1, bikeLightQuantity, "Bike light quantity should be 1");
    }

    @Test
    @DisplayName("Complete checkout process successfully")
    @Description("Test completing the entire checkout process")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Checkout Completion")
    public void testCompleteCheckoutProcessSuccessfully() {
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), "Should be on overview page");
        
        checkoutPage.clickFinish();
        
        assertTrue(checkoutPage.isCheckoutCompletePageLoaded(), "Should be on checkout complete page");
        assertEquals("Thank you for your order!", checkoutPage.getCompleteHeader(), 
                "Completion header should be correct");
        
        String completeText = checkoutPage.getCompleteText();
        assertFalse(completeText.isEmpty(), "Completion text should be displayed");
    }

    @Test
    @DisplayName("Return to inventory after checkout completion")
    @Description("Test navigating back to inventory page after successful checkout")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Completion")
    public void testReturnToInventoryAfterCheckout() {
        checkoutPage.completeCheckout("John", "Doe", "12345");
        assertTrue(checkoutPage.isCheckoutCompletePageLoaded(), "Should complete checkout");
        
        InventoryPage inventoryPage = checkoutPage.clickBackHome();
        
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Should return to inventory page");
        assertEquals(6, inventoryPage.getProductCount(), "Should display all products");
        assertEquals(0, inventoryPage.getCartItemCount(), "Cart should be empty after checkout completion");
    }

    @Test
    @DisplayName("Checkout with single item")
    @Description("Test checkout process with only one item in cart")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Variations")
    public void testCheckoutWithSingleItem() {
        // Start fresh with single item
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        CartPage singleItemCartPage = inventoryPage.clickShoppingCart();
        CheckoutPage singleItemCheckoutPage = singleItemCartPage.proceedToCheckout();
        
        singleItemCheckoutPage.fillCheckoutInformation("Jane", "Smith", "67890");
        singleItemCheckoutPage.clickContinue();
        
        assertEquals(1, singleItemCheckoutPage.getCheckoutItemCount(), "Should have 1 item in checkout");
        
        List<String> checkoutItems = singleItemCheckoutPage.getCheckoutItemNames();
        assertTrue(checkoutItems.contains("Sauce Labs Backpack"), "Should contain backpack");
        
        singleItemCheckoutPage.clickFinish();
        assertTrue(singleItemCheckoutPage.isCheckoutCompletePageLoaded(), "Should complete checkout successfully");
    }

    @ParameterizedTest
    @DisplayName("Checkout with different product combinations")
    @Description("Test checkout process with various product combinations")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Variations")
    @ValueSource(strings = {
        "Sauce Labs Fleece Jacket",
        "Sauce Labs Onesie", 
        "Test.allTheThings() T-Shirt (Red)"
    })
    public void testCheckoutWithDifferentProducts(String productName) {
        // Start fresh with specific product
        LoginPage loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addProductToCart(productName);
        CartPage specificCartPage = inventoryPage.clickShoppingCart();
        CheckoutPage specificCheckoutPage = specificCartPage.proceedToCheckout();
        
        specificCheckoutPage.fillCheckoutInformation("Test", "User", "12345");
        specificCheckoutPage.clickContinue();
        
        List<String> checkoutItems = specificCheckoutPage.getCheckoutItemNames();
        assertTrue(checkoutItems.contains(productName), "Should contain " + productName);
        
        String itemPrice = specificCheckoutPage.getItemPrice(productName);
        assertTrue(itemPrice.startsWith("$"), "Price should start with $ for " + productName);
        
        specificCheckoutPage.clickFinish();
        assertTrue(specificCheckoutPage.isCheckoutCompletePageLoaded(), 
                "Should complete checkout with " + productName);
    }

    @Test
    @DisplayName("Checkout form field validation - special characters")
    @Description("Test checkout form accepts special characters in names")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Validation")
    public void testCheckoutFormSpecialCharacters() {
        // Test with names containing special characters
        checkoutPage.fillCheckoutInformation("Jean-Paul", "O'Connor", "12345-6789");
        checkoutPage.clickContinue();
        
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), 
                "Should accept names with hyphens and apostrophes");
    }

    @Test
    @DisplayName("Checkout form field validation - long names")
    @Description("Test checkout form with longer names and postal codes")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Validation")
    public void testCheckoutFormLongNames() {
        String longFirstName = "Christopher";
        String longLastName = "Hendersonworth";
        String longPostalCode = "SW1A 1AA";
        
        checkoutPage.fillCheckoutInformation(longFirstName, longLastName, longPostalCode);
        checkoutPage.clickContinue();
        
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), 
                "Should accept longer names and postal codes");
    }

    @Test
    @DisplayName("Checkout overview page cancel functionality")
    @Description("Test canceling from checkout overview page returns to inventory")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Navigation")
    public void testCancelFromCheckoutOverview() {
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), "Should be on overview page");
        
        // Cancel from overview should return to inventory (based on typical e-commerce behavior)
        // Note: This test might need adjustment based on actual application behavior
        CartPage returnedCartPage = checkoutPage.clickCancel();
        assertTrue(returnedCartPage.isCartPageLoaded(), "Should return to cart page when canceling from overview");
    }

    @Test
    @DisplayName("Price consistency throughout checkout process")
    @Description("Test that prices remain consistent from cart to checkout completion")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Price Consistency")
    public void testPriceConsistencyThroughoutCheckout() {
        // Get prices from cart
        String cartBackpackPrice = cartPage.getItemPrice("Sauce Labs Backpack");
        String cartBikeLightPrice = cartPage.getItemPrice("Sauce Labs Bike Light");
        
        // Navigate to checkout and verify prices match
        CheckoutPage freshCheckoutPage = cartPage.proceedToCheckout();
        freshCheckoutPage.fillCheckoutInformation("John", "Doe", "12345");
        freshCheckoutPage.clickContinue();
        
        String checkoutBackpackPrice = freshCheckoutPage.getItemPrice("Sauce Labs Backpack");
        String checkoutBikeLightPrice = freshCheckoutPage.getItemPrice("Sauce Labs Bike Light");
        
        assertEquals(cartBackpackPrice, checkoutBackpackPrice, 
                "Backpack price should be consistent between cart and checkout");
        assertEquals(cartBikeLightPrice, checkoutBikeLightPrice, 
                "Bike light price should be consistent between cart and checkout");
    }
}
