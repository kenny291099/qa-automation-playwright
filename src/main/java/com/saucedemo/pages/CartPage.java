package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.util.List;
import java.util.ArrayList;

public class CartPage extends BasePage {

    // Cart elements
    private final Locator cartHeader = page.locator(".title");
    private final Locator continueShoppingButton = page.locator("[data-test='continue-shopping']");
    private final Locator checkoutButton = page.locator("[data-test='checkout']");
    private final Locator cartItems = page.locator(".cart_item");
    private final Locator cartItemNames = page.locator(".inventory_item_name");
    private final Locator cartItemPrices = page.locator(".inventory_item_price");

    // Header elements
    private final Locator shoppingCartBadge = page.locator(".shopping_cart_badge");

    @Step("Check if cart page is loaded")
    public boolean isCartPageLoaded() {
        return isVisible(cartHeader, "Cart header") &&
               getCurrentUrl().contains("/cart.html");
    }

    @Step("Get cart header text")
    public String getCartHeaderText() {
        return getText(cartHeader, "Cart header");
    }

    @Step("Get number of items in cart")
    public int getCartItemCount() {
        int count = cartItems.count();
        logger.info("Number of items in cart: {}", count);
        return count;
    }

    @Step("Check if cart is empty")
    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }

    @Step("Get all item names in cart")
    public List<String> getCartItemNames() {
        List<String> names = new ArrayList<>();
        int count = cartItemNames.count();
        for (int i = 0; i < count; i++) {
            names.add(cartItemNames.nth(i).textContent());
        }
        logger.info("Cart item names: {}", names);
        return names;
    }

    @Step("Get all item prices in cart")
    public List<String> getCartItemPrices() {
        List<String> prices = new ArrayList<>();
        int count = cartItemPrices.count();
        for (int i = 0; i < count; i++) {
            prices.add(cartItemPrices.nth(i).textContent());
        }
        logger.info("Cart item prices: {}", prices);
        return prices;
    }

    @Step("Get item quantity by name: {itemName}")
    public int getItemQuantity(String itemName) {
        Locator itemContainer = page.locator(".cart_item")
                .filter(new Locator.FilterOptions().setHas(page.locator(".inventory_item_name", 
                        new Page.LocatorOptions().setHasText(itemName))));
        Locator quantityElement = itemContainer.locator(".cart_quantity");
        String quantity = getText(quantityElement, "Quantity for " + itemName);
        return Integer.parseInt(quantity);
    }

    @Step("Remove item from cart by name: {itemName}")
    public CartPage removeItemFromCart(String itemName) {
        String buttonId = "remove-" + itemName.toLowerCase().replace(" ", "-");
        Locator removeButton = page.locator("#" + buttonId);
        click(removeButton, "Remove button for " + itemName);
        return this;
    }

    @Step("Check if item exists in cart: {itemName}")
    public boolean isItemInCart(String itemName) {
        List<String> itemNames = getCartItemNames();
        return itemNames.contains(itemName);
    }

    @Step("Get item price by name: {itemName}")
    public String getItemPrice(String itemName) {
        Locator itemContainer = page.locator(".cart_item")
                .filter(new Locator.FilterOptions().setHas(page.locator(".inventory_item_name", 
                        new Page.LocatorOptions().setHasText(itemName))));
        Locator priceElement = itemContainer.locator(".inventory_item_price");
        return getText(priceElement, "Price for " + itemName);
    }

    @Step("Get item description by name: {itemName}")
    public String getItemDescription(String itemName) {
        Locator itemContainer = page.locator(".cart_item")
                .filter(new Locator.FilterOptions().setHas(page.locator(".inventory_item_name", 
                        new Page.LocatorOptions().setHasText(itemName))));
        Locator descElement = itemContainer.locator(".inventory_item_desc");
        return getText(descElement, "Description for " + itemName);
    }

    @Step("Continue shopping")
    public InventoryPage continueShopping() {
        click(continueShoppingButton, "Continue shopping button");
        return new InventoryPage();
    }

    @Step("Proceed to checkout")
    public CheckoutPage proceedToCheckout() {
        click(checkoutButton, "Checkout button");
        return new CheckoutPage();
    }

    @Step("Get shopping cart badge count")
    public int getShoppingCartBadgeCount() {
        if (isVisible(shoppingCartBadge, "Shopping cart badge")) {
            String count = getText(shoppingCartBadge, "Shopping cart badge");
            return Integer.parseInt(count);
        }
        return 0;
    }

    @Step("Click on item name: {itemName}")
    public ProductDetailsPage clickItemName(String itemName) {
        Locator itemLink = page.locator(".inventory_item_name", new Page.LocatorOptions().setHasText(itemName));
        click(itemLink, "Item name: " + itemName);
        return new ProductDetailsPage();
    }

    @Step("Remove all items from cart")
    public CartPage removeAllItems() {
        List<String> itemNames = getCartItemNames();
        for (String itemName : itemNames) {
            removeItemFromCart(itemName);
        }
        return this;
    }
}
