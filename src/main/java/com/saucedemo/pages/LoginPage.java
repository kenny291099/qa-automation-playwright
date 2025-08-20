package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

public class LoginPage extends BasePage {
    
    // Locators
    private final Locator usernameField = page.locator("[data-test='username']");
    private final Locator passwordField = page.locator("[data-test='password']");
    private final Locator loginButton = page.locator("[data-test='login-button']");
    private final Locator errorMessage = page.locator("[data-test='error']");
    private final Locator errorButton = page.locator(".error-button");
    private final Locator loginLogo = page.locator(".login_logo");
    private final Locator credentialsContainer = page.locator("#login_credentials");

    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        fill(usernameField, username, "Username field");
        return this;
    }

    @Step("Enter password: {password}")
    public LoginPage enterPassword(String password) {
        fill(passwordField, password, "Password field");
        return this;
    }

    @Step("Click login button")
    public InventoryPage clickLogin() {
        click(loginButton, "Login button");
        return new InventoryPage();
    }

    @Step("Login with credentials - Username: {username}, Password: {password}")
    public InventoryPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    @Step("Login with invalid credentials - Username: {username}, Password: {password}")
    public LoginPage loginWithInvalidCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton, "Login button");
        return this;
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
    public LoginPage clearErrorMessage() {
        if (isVisible(errorButton, "Error close button")) {
            click(errorButton, "Error close button");
        }
        return this;
    }

    @Step("Check if login form is displayed")
    public boolean isLoginFormDisplayed() {
        return isVisible(usernameField, "Username field") &&
               isVisible(passwordField, "Password field") &&
               isVisible(loginButton, "Login button");
    }

    @Step("Get login logo text")
    public String getLoginLogoText() {
        return getText(loginLogo, "Login logo");
    }

    @Step("Get accepted usernames")
    public String getAcceptedUsernames() {
        return getText(credentialsContainer, "Credentials container");
    }

    @Step("Check if username field is enabled")
    public boolean isUsernameFieldEnabled() {
        return isEnabled(usernameField, "Username field");
    }

    @Step("Check if password field is enabled")
    public boolean isPasswordFieldEnabled() {
        return isEnabled(passwordField, "Password field");
    }

    @Step("Check if login button is enabled")
    public boolean isLoginButtonEnabled() {
        return isEnabled(loginButton, "Login button");
    }

    @Step("Clear login form")
    public LoginPage clearForm() {
        fill(usernameField, "", "Username field");
        fill(passwordField, "", "Password field");
        return this;
    }
}
