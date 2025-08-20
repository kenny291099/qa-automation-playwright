package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Sauce Demo E-commerce")
@Feature("Login Functionality")
@Owner("QA Team")
public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Successful login with valid credentials")
    @Description("Test successful login with standard user credentials")
    @Severity(SeverityLevel.BLOCKER)
    @Story("User Authentication")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage();
        
        assertTrue(loginPage.isLoginFormDisplayed(), "Login form should be displayed");
        assertEquals("Swag Labs", loginPage.getLoginLogoText(), "Login logo text should be correct");
        
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
        
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page should be loaded after successful login");
        assertEquals("Swag Labs", inventoryPage.getAppLogoText(), "App logo should be displayed");
        assertTrue(inventoryPage.getProductCount() > 0, "Products should be displayed");
    }

    @Test
    @DisplayName("Login attempt with invalid password")
    @Description("Test login failure with invalid password")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Authentication")
    public void testLoginWithInvalidPassword() {
        LoginPage loginPage = new LoginPage();
        
        loginPage.loginWithInvalidCredentials("standard_user", "invalid_password");
        
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        String errorMessage = loginPage.getErrorMessage();
        assertTrue(errorMessage.contains("Username and password do not match"), 
                "Error message should indicate invalid credentials");
        
        // Verify user remains on login page
        assertTrue(loginPage.isLoginFormDisplayed(), "User should remain on login page");
    }

    @Test
    @DisplayName("Login attempt with locked out user")
    @Description("Test login failure with locked out user account")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Authentication")
    public void testLoginWithLockedOutUser() {
        LoginPage loginPage = new LoginPage();
        
        loginPage.loginWithInvalidCredentials("locked_out_user", "secret_sauce");
        
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        String errorMessage = loginPage.getErrorMessage();
        assertTrue(errorMessage.contains("Sorry, this user has been locked out"), 
                "Error message should indicate user is locked out");
    }

    @ParameterizedTest
    @DisplayName("Login attempts with empty fields")
    @Description("Test login failures with empty username or password fields")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Authentication")
    @CsvSource({
        "'', 'secret_sauce', 'Username is required'",
        "'standard_user', '', 'Password is required'",
        "'', '', 'Username is required'"
    })
    public void testLoginWithEmptyFields(String username, String password, String expectedErrorPart) {
        LoginPage loginPage = new LoginPage();
        
        loginPage.loginWithInvalidCredentials(username, password);
        
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        String errorMessage = loginPage.getErrorMessage();
        assertTrue(errorMessage.contains(expectedErrorPart), 
                "Error message should contain: " + expectedErrorPart);
    }

    @ParameterizedTest
    @DisplayName("Login with different invalid usernames")
    @Description("Test login failures with various invalid usernames")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Authentication")
    @ValueSource(strings = {"invalid_user", "test_user", "admin", "user123"})
    public void testLoginWithInvalidUsernames(String username) {
        LoginPage loginPage = new LoginPage();
        
        loginPage.loginWithInvalidCredentials(username, "secret_sauce");
        
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        String errorMessage = loginPage.getErrorMessage();
        assertTrue(errorMessage.contains("Username and password do not match"), 
                "Error message should indicate invalid credentials");
    }

    @Test
    @DisplayName("Clear error message functionality")
    @Description("Test clearing error message by clicking the X button")
    @Severity(SeverityLevel.MINOR)
    @Story("User Authentication")
    public void testClearErrorMessage() {
        LoginPage loginPage = new LoginPage();
        
        // Generate an error first
        loginPage.loginWithInvalidCredentials("", "secret_sauce");
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        
        // Clear the error
        loginPage.clearErrorMessage();
        assertFalse(loginPage.isErrorMessageDisplayed(), "Error message should be cleared");
    }

    @Test
    @DisplayName("Login form field accessibility")
    @Description("Test that all login form fields are accessible and enabled")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Authentication")
    public void testLoginFormAccessibility() {
        LoginPage loginPage = new LoginPage();
        
        assertTrue(loginPage.isUsernameFieldEnabled(), "Username field should be enabled");
        assertTrue(loginPage.isPasswordFieldEnabled(), "Password field should be enabled");
        assertTrue(loginPage.isLoginButtonEnabled(), "Login button should be enabled");
        assertTrue(loginPage.isLoginFormDisplayed(), "Login form should be fully displayed");
    }

    @Test
    @DisplayName("Login with problem user")
    @Description("Test login with problem user to verify system behavior")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Authentication")
    public void testLoginWithProblemUser() {
        LoginPage loginPage = new LoginPage();
        
        InventoryPage inventoryPage = loginPage.login("problem_user", "secret_sauce");
        
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page should be loaded");
        // Problem user might have issues with images, but login should still work
        assertTrue(inventoryPage.getProductCount() > 0, "Products should be displayed");
    }

    @Test
    @DisplayName("Login with performance glitch user")
    @Description("Test login with performance glitch user")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Authentication")
    public void testLoginWithPerformanceGlitchUser() {
        LoginPage loginPage = new LoginPage();
        
        InventoryPage inventoryPage = loginPage.login("performance_glitch_user", "secret_sauce");
        
        assertTrue(inventoryPage.isInventoryPageLoaded(), "Inventory page should be loaded");
        assertTrue(inventoryPage.getProductCount() > 0, "Products should be displayed");
    }

    @Test
    @DisplayName("Verify accepted usernames are displayed")
    @Description("Test that the accepted usernames information is visible on login page")
    @Severity(SeverityLevel.TRIVIAL)
    @Story("User Authentication")
    public void testAcceptedUsernamesDisplayed() {
        LoginPage loginPage = new LoginPage();
        
        String credentialsInfo = loginPage.getAcceptedUsernames();
        assertFalse(credentialsInfo.isEmpty(), "Credentials information should be displayed");
        assertTrue(credentialsInfo.contains("standard_user"), "Should show standard_user as accepted username");
    }

    @Test
    @DisplayName("Form clearing functionality")
    @Description("Test that form fields can be cleared")
    @Severity(SeverityLevel.TRIVIAL)
    @Story("User Authentication")
    public void testFormClearing() {
        LoginPage loginPage = new LoginPage();
        
        // Fill the form
        loginPage.enterUsername("test_user").enterPassword("test_password");
        
        // Clear the form
        loginPage.clearForm();
        
        // Try to login with cleared form (should show required field errors)
        loginPage.clickLogin();
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error should be shown for empty fields");
    }
}
