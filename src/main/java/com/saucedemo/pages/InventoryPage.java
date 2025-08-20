package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.util.List;
import java.util.ArrayList;

public class InventoryPage extends BasePage {

    // Header elements
    private final Locator appLogo = page.locator(".app_logo");
    private final Locator shoppingCartLink = page.locator(".shopping_cart_link");
    private final Locator shoppingCartBadge = page.locator(".shopping_cart_badge");
    private final Locator menuButton = page.locator("#react-burger-menu-btn");
    private final Locator sortDropdown = page.locator("[data-test='product_sort_container']");

    // Product elements
    private final Locator inventoryContainer = page.locator(".inventory_container");
    private final Locator inventoryItems = page.locator(".inventory_item");
    private final Locator inventoryItemNames = page.locator(".inventory_item_name");
    private final Locator inventoryItemPrices = page.locator(".inventory_item_price");

    // Menu elements
    private final Locator logoutLink = page.locator("#logout_sidebar_link");
    private final Locator resetAppStateLink = page.locator("#reset_sidebar_link");
    private final Locator closeMenuButton = page.locator("#react-burger-cross-btn");

    @Step("Check if inventory page is loaded")
    public boolean isInventoryPageLoaded() {
        return isVisible(inventoryContainer, "Inventory container") &&
               isVisible(appLogo, "App logo");
    }

    @Step("Get app logo text")
    public String getAppLogoText() {
        return getText(appLogo, "App logo");
    }

    @Step("Get number of products")
    public int getProductCount() {
        int count = inventoryItems.count();
        logger.info("Number of products: {}", count);
        return count;
    }

    @Step("Get all product names")
    public List<String> getProductNames() {
        List<String> names = new ArrayList<>();
        int count = inventoryItemNames.count();
        for (int i = 0; i < count; i++) {
            names.add(inventoryItemNames.nth(i).textContent());
        }
        logger.info("Product names: {}", names);
        return names;
    }

    @Step("Get all product prices")
    public List<String> getProductPrices() {
        List<String> prices = new ArrayList<>();
        int count = inventoryItemPrices.count();
        for (int i = 0; i < count; i++) {
            prices.add(inventoryItemPrices.nth(i).textContent());
        }
        logger.info("Product prices: {}", prices);
        return prices;
    }

    @Step("Add product to cart by name: {productName}")
    public InventoryPage addProductToCart(String productName) {
        String buttonId = "add-to-cart-" + productName.toLowerCase().replace(" ", "-");
        Locator addButton = page.locator("#" + buttonId);
        click(addButton, "Add to cart button for " + productName);
        return this;
    }

    @Step("Remove product from cart by name: {productName}")
    public InventoryPage removeProductFromCart(String productName) {
        String buttonId = "remove-" + productName.toLowerCase().replace(" ", "-");
        Locator removeButton = page.locator("#" + buttonId);
        click(removeButton, "Remove button for " + productName);
        return this;
    }

    @Step("Click on product name: {productName}")
    public ProductDetailsPage clickProductName(String productName) {
        Locator productLink = page.locator(".inventory_item_name", new Page.LocatorOptions().setHasText(productName));
        click(productLink, "Product name: " + productName);
        return new ProductDetailsPage();
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

    @Step("Sort products by: {sortOption}")
    public InventoryPage sortProducts(String sortOption) {
        selectOption(sortDropdown, sortOption, "Sort dropdown");
        return this;
    }

    @Step("Open menu")
    public InventoryPage openMenu() {
        click(menuButton, "Menu button");
        return this;
    }

    @Step("Close menu")
    public InventoryPage closeMenu() {
        if (isVisible(closeMenuButton, "Close menu button")) {
            click(closeMenuButton, "Close menu button");
        }
        return this;
    }

    @Step("Logout")
    public LoginPage logout() {
        openMenu();
        click(logoutLink, "Logout link");
        return new LoginPage();
    }

    @Step("Reset app state")
    public InventoryPage resetAppState() {
        openMenu();
        click(resetAppStateLink, "Reset app state link");
        closeMenu();
        return this;
    }

    @Step("Check if product is in cart: {productName}")
    public boolean isProductInCart(String productName) {
        String buttonId = "remove-" + productName.toLowerCase().replace(" ", "-");
        Locator removeButton = page.locator("#" + buttonId);
        return isVisible(removeButton, "Remove button for " + productName);
    }

    @Step("Get product price by name: {productName}")
    public String getProductPrice(String productName) {
        Locator productContainer = page.locator(".inventory_item")
                .filter(new Locator.FilterOptions().setHas(page.locator(".inventory_item_name", 
                        new Page.LocatorOptions().setHasText(productName))));
        Locator priceElement = productContainer.locator(".inventory_item_price");
        return getText(priceElement, "Price for " + productName);
    }

    @Step("Get product description by name: {productName}")
    public String getProductDescription(String productName) {
        Locator productContainer = page.locator(".inventory_item")
                .filter(new Locator.FilterOptions().setHas(page.locator(".inventory_item_name", 
                        new Page.LocatorOptions().setHasText(productName))));
        Locator descElement = productContainer.locator(".inventory_item_desc");
        return getText(descElement, "Description for " + productName);
    }
}
