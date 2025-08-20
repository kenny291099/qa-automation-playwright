package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.util.List;
import java.util.ArrayList;

public class CheckoutPage extends BasePage {

    // Checkout information form elements
    private final Locator checkoutTitle = page.locator(".title");
    private final Locator firstNameField = page.locator("[data-test='firstName']");
    private final Locator lastNameField = page.locator("[data-test='lastName']");
    private final Locator postalCodeField = page.locator("[data-test='postalCode']");
    private final Locator continueButton = page.locator("[data-test='continue']");
    private final Locator cancelButton = page.locator("[data-test='cancel']");
    private final Locator errorMessage = page.locator("[data-test='error']");
    private final Locator errorButton = page.locator(".error-button");

    // Checkout overview elements
    private final Locator finishButton = page.locator("[data-test='finish']");
    private final Locator cartItems = page.locator(".cart_item");
    private final Locator cartItemNames = page.locator(".inventory_item_name");
    private final Locator cartItemPrices = page.locator(".inventory_item_price");
    private final Locator cartItemQuantities = page.locator(".cart_quantity");
    private final Locator paymentInformation = page.locator("[data-test='payment-info-value']");
    private final Locator shippingInformation = page.locator("[data-test='shipping-info-value']");
    private final Locator itemTotal = page.locator(".summary_subtotal_label");
    private final Locator tax = page.locator(".summary_tax_label");
    private final Locator total = page.locator(".summary_total_label");

    // Checkout complete elements
    private final Locator completeHeader = page.locator(".complete-header");
    private final Locator completeText = page.locator(".complete-text");
    private final Locator backHomeButton = page.locator("[data-test='back-to-products']");

    @Step("Check if checkout information page is loaded")
    public boolean isCheckoutInformationPageLoaded() {
        return getCurrentUrl().contains("/checkout-step-one.html") &&
               isVisible(firstNameField, "First name field");
    }

    @Step("Check if checkout overview page is loaded")
    public boolean isCheckoutOverviewPageLoaded() {
        return getCurrentUrl().contains("/checkout-step-two.html") &&
               isVisible(finishButton, "Finish button");
    }

    @Step("Check if checkout complete page is loaded")
    public boolean isCheckoutCompletePageLoaded() {
        return getCurrentUrl().contains("/checkout-complete.html") &&
               isVisible(completeHeader, "Complete header");
    }

    @Step("Get checkout page title")
    public String getCheckoutTitle() {
        return getText(checkoutTitle, "Checkout title");
    }

    @Step("Enter first name: {firstName}")
    public CheckoutPage enterFirstName(String firstName) {
        fill(firstNameField, firstName, "First name field");
        return this;
    }

    @Step("Enter last name: {lastName}")
    public CheckoutPage enterLastName(String lastName) {
        fill(lastNameField, lastName, "Last name field");
        return this;
    }

    @Step("Enter postal code: {postalCode}")
    public CheckoutPage enterPostalCode(String postalCode) {
        fill(postalCodeField, postalCode, "Postal code field");
        return this;
    }

    @Step("Fill checkout information - First Name: {firstName}, Last Name: {lastName}, Postal Code: {postalCode}")
    public CheckoutPage fillCheckoutInformation(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        return this;
    }

    @Step("Click continue button")
    public CheckoutPage clickContinue() {
        click(continueButton, "Continue button");
        return this;
    }

    @Step("Click cancel button")
    public CartPage clickCancel() {
        click(cancelButton, "Cancel button");
        return new CartPage();
    }

    @Step("Get error message")
    public String getErrorMessage() {
        if (isVisible(errorMessage, "Error message")) {
            return getText(errorMessage, "Error message");
        }
        return "";
    }

    @Step("Check if error message is displayed")
    public boolean isErrorMessageDisplayed() {
        return isVisible(errorMessage, "Error message");
    }

    @Step("Clear error message")
    public CheckoutPage clearErrorMessage() {
        if (isVisible(errorButton, "Error close button")) {
            click(errorButton, "Error close button");
        }
        return this;
    }

    @Step("Get cart items in checkout overview")
    public List<String> getCheckoutItemNames() {
        List<String> names = new ArrayList<>();
        int count = cartItemNames.count();
        for (int i = 0; i < count; i++) {
            names.add(cartItemNames.nth(i).textContent());
        }
        logger.info("Checkout item names: {}", names);
        return names;
    }

    @Step("Get payment information")
    public String getPaymentInformation() {
        return getText(paymentInformation, "Payment information");
    }

    @Step("Get shipping information")
    public String getShippingInformation() {
        return getText(shippingInformation, "Shipping information");
    }

    @Step("Get item total")
    public String getItemTotal() {
        return getText(itemTotal, "Item total");
    }

    @Step("Get tax amount")
    public String getTax() {
        return getText(tax, "Tax");
    }

    @Step("Get total amount")
    public String getTotal() {
        return getText(total, "Total");
    }

    @Step("Click finish button")
    public CheckoutPage clickFinish() {
        click(finishButton, "Finish button");
        return this;
    }

    @Step("Get completion header text")
    public String getCompleteHeader() {
        return getText(completeHeader, "Complete header");
    }

    @Step("Get completion text")
    public String getCompleteText() {
        return getText(completeText, "Complete text");
    }

    @Step("Click back home button")
    public InventoryPage clickBackHome() {
        click(backHomeButton, "Back home button");
        return new InventoryPage();
    }

    @Step("Complete checkout process with information - First Name: {firstName}, Last Name: {lastName}, Postal Code: {postalCode}")
    public CheckoutPage completeCheckout(String firstName, String lastName, String postalCode) {
        if (isCheckoutInformationPageLoaded()) {
            fillCheckoutInformation(firstName, lastName, postalCode);
            clickContinue();
        }
        
        if (isCheckoutOverviewPageLoaded()) {
            clickFinish();
        }
        
        return this;
    }

    @Step("Get number of items in checkout")
    public int getCheckoutItemCount() {
        int count = cartItems.count();
        logger.info("Number of items in checkout: {}", count);
        return count;
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

    @Step("Get item price by name: {itemName}")
    public String getItemPrice(String itemName) {
        Locator itemContainer = page.locator(".cart_item")
                .filter(new Locator.FilterOptions().setHas(page.locator(".inventory_item_name", 
                        new Page.LocatorOptions().setHasText(itemName))));
        Locator priceElement = itemContainer.locator(".inventory_item_price");
        return getText(priceElement, "Price for " + itemName);
    }
}
