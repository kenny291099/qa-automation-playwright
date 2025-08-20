package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

public class ProductDetailsPage extends BasePage {

    // Product details elements
    private final Locator backToProductsButton = page.locator("[data-test='back-to-products']");
    private final Locator productImage = page.locator(".inventory_details_img");
    private final Locator productName = page.locator(".inventory_details_name");
    private final Locator productDescription = page.locator(".inventory_details_desc");
    private final Locator productPrice = page.locator(".inventory_details_price");
    private final Locator addToCartButton = page.locator("button[id*='add-to-cart']");
    private final Locator removeButton = page.locator("button[id*='remove']");

    // Header elements
    private final Locator shoppingCartLink = page.locator(".shopping_cart_link");
    private final Locator shoppingCartBadge = page.locator(".shopping_cart_badge");

    @Step("Check if product details page is loaded")
    public boolean isProductDetailsPageLoaded() {
        return isVisible(productName, "Product name") &&
               isVisible(productDescription, "Product description") &&
               isVisible(productPrice, "Product price");
    }

    @Step("Get product name")
    public String getProductName() {
        return getText(productName, "Product name");
    }

    @Step("Get product description")
    public String getProductDescription() {
        return getText(productDescription, "Product description");
    }

    @Step("Get product price")
    public String getProductPrice() {
        return getText(productPrice, "Product price");
    }

    @Step("Check if product image is displayed")
    public boolean isProductImageDisplayed() {
        return isVisible(productImage, "Product image");
    }

    @Step("Add product to cart")
    public ProductDetailsPage addToCart() {
        if (isVisible(addToCartButton, "Add to cart button")) {
            click(addToCartButton, "Add to cart button");
        }
        return this;
    }

    @Step("Remove product from cart")
    public ProductDetailsPage removeFromCart() {
        if (isVisible(removeButton, "Remove button")) {
            click(removeButton, "Remove button");
        }
        return this;
    }

    @Step("Check if product is added to cart")
    public boolean isProductAddedToCart() {
        return isVisible(removeButton, "Remove button");
    }

    @Step("Get cart item count")
    public int getCartItemCount() {
        if (isVisible(shoppingCartBadge, "Shopping cart badge")) {
            String count = getText(shoppingCartBadge, "Shopping cart badge");
            return Integer.parseInt(count);
        }
        return 0;
    }

    @Step("Click shopping cart")
    public CartPage clickShoppingCart() {
        click(shoppingCartLink, "Shopping cart link");
        return new CartPage();
    }

    @Step("Go back to products")
    public InventoryPage goBackToProducts() {
        click(backToProductsButton, "Back to products button");
        return new InventoryPage();
    }

    @Step("Get product image source")
    public String getProductImageSrc() {
        return getAttribute(productImage, "src", "Product image");
    }
}
