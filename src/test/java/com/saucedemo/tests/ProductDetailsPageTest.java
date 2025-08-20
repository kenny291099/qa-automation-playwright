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
@Feature("Product Details Management")
@Owner("QA Team")
public class ProductDetailsPageTest extends BaseTest {

    private ProductDetailsPage productDetailsPage;
    private InventoryPage inventoryPage;
    private String testProductName;

    @BeforeEach
    void setupProductDetailsTests() {
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login("standard_user", "secret_sauce");
        testProductName = "Sauce Labs Backpack";
        productDetailsPage = inventoryPage.clickProductName(testProductName);
    }

    @Test
    @DisplayName("Verify product details page loads correctly")
    @Description("Test that product details page loads with all required elements")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Product Details Display")
    public void testProductDetailsPageLoading() {
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Product details page should be loaded");
        assertEquals(testProductName, productDetailsPage.getProductName(), "Product name should match");
        assertFalse(productDetailsPage.getProductDescription().isEmpty(), "Product description should be displayed");
        assertFalse(productDetailsPage.getProductPrice().isEmpty(), "Product price should be displayed");
    }

    @Test
    @DisplayName("Verify product image is displayed")
    @Description("Test that product image is visible on details page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Details Display")
    public void testProductImageDisplay() {
        assertTrue(productDetailsPage.isProductImageDisplayed(), "Product image should be displayed");
        
        String imageSrc = productDetailsPage.getProductImageSrc();
        assertFalse(imageSrc.isEmpty(), "Product image should have source");
        assertTrue(imageSrc.contains(".jpg") || imageSrc.contains(".png") || imageSrc.contains(".gif"), 
                "Image source should be a valid image format");
    }

    @Test
    @DisplayName("Verify product information accuracy")
    @Description("Test that product information matches inventory page data")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Details Display")
    public void testProductInformationAccuracy() {
        // Go back to inventory to get expected values
        InventoryPage returnedInventoryPage = productDetailsPage.goBackToProducts();
        String expectedPrice = returnedInventoryPage.getProductPrice(testProductName);
        String expectedDescription = returnedInventoryPage.getProductDescription(testProductName);
        
        // Return to product details
        ProductDetailsPage refreshedDetailsPage = returnedInventoryPage.clickProductName(testProductName);
        
        assertEquals(expectedPrice, refreshedDetailsPage.getProductPrice(), "Price should match inventory page");
        assertEquals(expectedDescription, refreshedDetailsPage.getProductDescription(), "Description should match inventory page");
        assertEquals(testProductName, refreshedDetailsPage.getProductName(), "Product name should be consistent");
    }

    @Test
    @DisplayName("Add product to cart from details page")
    @Description("Test adding product to cart from product details page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Cart Management")
    public void testAddProductToCartFromDetailsPage() {
        assertEquals(0, productDetailsPage.getCartItemCount(), "Cart should be empty initially");
        assertFalse(productDetailsPage.isProductAddedToCart(), "Product should not be in cart initially");
        
        productDetailsPage.addToCart();
        
        assertEquals(1, productDetailsPage.getCartItemCount(), "Cart should have 1 item after adding");
        assertTrue(productDetailsPage.isProductAddedToCart(), "Product should be marked as added to cart");
    }

    @Test
    @DisplayName("Remove product from cart on details page")
    @Description("Test removing product from cart using details page button")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Cart Management")
    public void testRemoveProductFromCartOnDetailsPage() {
        // Add product first
        productDetailsPage.addToCart();
        assertTrue(productDetailsPage.isProductAddedToCart(), "Product should be in cart");
        assertEquals(1, productDetailsPage.getCartItemCount(), "Cart should have 1 item");
        
        // Remove product
        productDetailsPage.removeFromCart();
        
        assertFalse(productDetailsPage.isProductAddedToCart(), "Product should be removed from cart");
        assertEquals(0, productDetailsPage.getCartItemCount(), "Cart should be empty after removal");
    }

    @Test
    @DisplayName("Navigate to cart from product details page")
    @Description("Test clicking shopping cart icon navigates to cart page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Details Navigation")
    public void testNavigateToCartFromDetailsPage() {
        // Add product to cart first
        productDetailsPage.addToCart();
        assertEquals(1, productDetailsPage.getCartItemCount(), "Cart should have 1 item");
        
        CartPage cartPage = productDetailsPage.clickShoppingCart();
        
        assertTrue(cartPage.isCartPageLoaded(), "Should navigate to cart page");
        assertEquals(1, cartPage.getCartItemCount(), "Cart should have 1 item");
        assertTrue(cartPage.isItemInCart(testProductName), "Product should be in cart");
    }

    @Test
    @DisplayName("Navigate back to inventory from product details")
    @Description("Test back to products button functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Details Navigation")
    public void testNavigateBackToInventory() {
        InventoryPage returnedInventoryPage = productDetailsPage.goBackToProducts();
        
        assertTrue(returnedInventoryPage.isInventoryPageLoaded(), "Should return to inventory page");
        assertEquals(6, returnedInventoryPage.getProductCount(), "Should display all products");
    }

    @Test
    @DisplayName("Cart state persistence during navigation")
    @Description("Test that cart state is preserved when navigating between pages")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Cart State Management")
    public void testCartStatePersistenceDuringNavigation() {
        // Add product to cart
        productDetailsPage.addToCart();
        assertEquals(1, productDetailsPage.getCartItemCount(), "Cart should have 1 item");
        
        // Navigate to inventory and back
        InventoryPage returnedInventoryPage = productDetailsPage.goBackToProducts();
        assertEquals(1, returnedInventoryPage.getCartItemCount(), "Cart count should be preserved on inventory page");
        
        ProductDetailsPage refreshedDetailsPage = returnedInventoryPage.clickProductName(testProductName);
        assertEquals(1, refreshedDetailsPage.getCartItemCount(), "Cart count should be preserved on details page");
        assertTrue(refreshedDetailsPage.isProductAddedToCart(), "Product should still be marked as in cart");
        
        // Navigate to cart and back
        CartPage cartPage = refreshedDetailsPage.clickShoppingCart();
        assertTrue(cartPage.isItemInCart(testProductName), "Product should be in cart");
        
        ProductDetailsPage finalDetailsPage = cartPage.clickItemName(testProductName);
        assertTrue(finalDetailsPage.isProductAddedToCart(), "Product should still be marked as in cart after cart navigation");
    }

    @ParameterizedTest
    @DisplayName("Verify product details for different products")
    @Description("Test product details page functionality with various products")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Details Display")
    @ValueSource(strings = {
        "Sauce Labs Bike Light",
        "Sauce Labs Bolt T-Shirt",
        "Sauce Labs Fleece Jacket", 
        "Sauce Labs Onesie",
        "Test.allTheThings() T-Shirt (Red)"
    })
    public void testProductDetailsForDifferentProducts(String productName) {
        // Navigate back to inventory and select different product
        InventoryPage returnedInventoryPage = productDetailsPage.goBackToProducts();
        ProductDetailsPage specificProductPage = returnedInventoryPage.clickProductName(productName);
        
        assertTrue(specificProductPage.isProductDetailsPageLoaded(), 
                "Product details page should load for " + productName);
        assertEquals(productName, specificProductPage.getProductName(), 
                "Product name should match for " + productName);
        
        // Verify essential elements are present
        assertFalse(specificProductPage.getProductDescription().isEmpty(), 
                "Product description should be present for " + productName);
        
        String price = specificProductPage.getProductPrice();
        assertTrue(price.startsWith("$"), "Price should start with $ for " + productName);
        assertTrue(price.matches("\\$\\d+\\.\\d{2}"), "Price should be in format $XX.XX for " + productName);
        
        assertTrue(specificProductPage.isProductImageDisplayed(), 
                "Product image should be displayed for " + productName);
    }

    @Test
    @DisplayName("Add multiple products to cart via details pages")
    @Description("Test adding multiple products to cart by navigating through details pages")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Cart Management")
    public void testAddMultipleProductsViaDetailsPages() {
        String[] products = {"Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt"};
        
        // Add first product (already on backpack details page)
        productDetailsPage.addToCart();
        assertEquals(1, productDetailsPage.getCartItemCount(), "Cart should have 1 item");
        
        // Navigate to other products and add them
        InventoryPage currentInventoryPage = productDetailsPage.goBackToProducts();
        
        for (int i = 1; i < products.length; i++) {
            ProductDetailsPage currentDetailsPage = currentInventoryPage.clickProductName(products[i]);
            currentDetailsPage.addToCart();
            assertEquals(i + 1, currentDetailsPage.getCartItemCount(), 
                    "Cart should have " + (i + 1) + " items after adding " + products[i]);
            currentInventoryPage = currentDetailsPage.goBackToProducts();
        }
        
        // Verify all products are in cart
        CartPage finalCartPage = currentInventoryPage.clickShoppingCart();
        assertEquals(3, finalCartPage.getCartItemCount(), "Cart should have 3 items total");
        
        for (String product : products) {
            assertTrue(finalCartPage.isItemInCart(product), 
                    "Cart should contain " + product);
        }
    }

    @Test
    @DisplayName("Product details page URL verification")
    @Description("Test that product details page has correct URL pattern")
    @Severity(SeverityLevel.MINOR)
    @Story("Product Details Display")
    public void testProductDetailsPageURL() {
        // URL should contain inventory-item and product identifier
        String currentUrl = productDetailsPage.getCurrentUrl();
        assertTrue(currentUrl.contains("inventory-item.html"), 
                "URL should contain inventory-item.html for product details");
        assertTrue(currentUrl.contains("id="), 
                "URL should contain product ID parameter");
    }

    @Test
    @DisplayName("Product price format validation on details page")
    @Description("Test that product price is properly formatted on details page")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Details Display")
    public void testProductPriceFormatValidation() {
        String price = productDetailsPage.getProductPrice();
        
        assertTrue(price.startsWith("$"), "Price should start with $");
        assertTrue(price.matches("\\$\\d+\\.\\d{2}"), "Price should be in format $XX.XX");
        assertFalse(price.contains("undefined"), "Price should not contain undefined");
        assertFalse(price.contains("null"), "Price should not contain null");
    }

    @Test
    @DisplayName("Product description content validation")
    @Description("Test that product description contains meaningful content")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Details Display")
    public void testProductDescriptionContentValidation() {
        String description = productDetailsPage.getProductDescription();
        
        assertFalse(description.isEmpty(), "Description should not be empty");
        assertTrue(description.length() > 10, "Description should be meaningful (more than 10 characters)");
        assertFalse(description.equals("undefined"), "Description should not be undefined");
        assertFalse(description.equals("null"), "Description should not be null");
    }

    @Test
    @DisplayName("Product details page accessibility elements")
    @Description("Test that key interactive elements are accessible on product details page")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Details Accessibility")
    public void testProductDetailsPageAccessibility() {
        // Test that key interactive elements are present and accessible
        assertTrue(productDetailsPage.isProductDetailsPageLoaded(), "Page should be fully loaded");
        assertTrue(productDetailsPage.isProductImageDisplayed(), "Product image should be accessible");
        
        // Add to cart functionality should be available
        productDetailsPage.addToCart();
        assertTrue(productDetailsPage.isProductAddedToCart(), "Add to cart functionality should work");
        
        // Remove functionality should be available after adding
        productDetailsPage.removeFromCart();
        assertFalse(productDetailsPage.isProductAddedToCart(), "Remove functionality should work");
    }

    @Test
    @DisplayName("Cross-browser product details consistency")
    @Description("Test that product details remain consistent across navigation patterns")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Details Consistency")
    public void testProductDetailsConsistencyAcrossNavigation() {
        // Capture initial details
        String originalName = productDetailsPage.getProductName();
        String originalPrice = productDetailsPage.getProductPrice();
        String originalDescription = productDetailsPage.getProductDescription();
        
        // Navigate away and back via inventory
        InventoryPage inventoryPage = productDetailsPage.goBackToProducts();
        ProductDetailsPage refreshedDetailsPage = inventoryPage.clickProductName(originalName);
        
        assertEquals(originalName, refreshedDetailsPage.getProductName(), "Name should be consistent");
        assertEquals(originalPrice, refreshedDetailsPage.getProductPrice(), "Price should be consistent");
        assertEquals(originalDescription, refreshedDetailsPage.getProductDescription(), "Description should be consistent");
        
        // Navigate away and back via cart (if product is in cart)
        refreshedDetailsPage.addToCart();
        CartPage cartPage = refreshedDetailsPage.clickShoppingCart();
        ProductDetailsPage cartNavigatedDetailsPage = cartPage.clickItemName(originalName);
        
        assertEquals(originalName, cartNavigatedDetailsPage.getProductName(), "Name should be consistent via cart navigation");
        assertEquals(originalPrice, cartNavigatedDetailsPage.getProductPrice(), "Price should be consistent via cart navigation");
        assertEquals(originalDescription, cartNavigatedDetailsPage.getProductDescription(), "Description should be consistent via cart navigation");
    }

    @Test
    @DisplayName("Product details page performance validation")
    @Description("Test that product details page loads efficiently")
    @Severity(SeverityLevel.MINOR)
    @Story("Product Details Performance")
    public void testProductDetailsPagePerformance() {
        // Navigate to a different product to test loading
        InventoryPage inventoryPage = productDetailsPage.goBackToProducts();
        
        long startTime = System.currentTimeMillis();
        ProductDetailsPage newDetailsPage = inventoryPage.clickProductName("Sauce Labs Bike Light");
        long endTime = System.currentTimeMillis();
        
        assertTrue(newDetailsPage.isProductDetailsPageLoaded(), "Page should load successfully");
        
        // Basic performance check - page should load within reasonable time
        long loadTime = endTime - startTime;
        assertTrue(loadTime < 5000, "Page should load within 5 seconds, but took " + loadTime + "ms");
    }
}
