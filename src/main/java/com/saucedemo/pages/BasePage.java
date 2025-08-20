package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.utils.BrowserManager;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage {
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected final Page page;

    public BasePage() {
        this.page = BrowserManager.getPage();
    }

    @Step("Wait for page to load")
    public void waitForPageLoad() {
        page.waitForLoadState();
        logger.info("Page loaded: {}", page.url());
    }

    @Step("Get page title")
    public String getPageTitle() {
        String title = page.title();
        logger.info("Page title: {}", title);
        return title;
    }

    @Step("Get current URL")
    public String getCurrentUrl() {
        String url = page.url();
        logger.info("Current URL: {}", url);
        return url;
    }

    @Step("Click element: {locatorDescription}")
    protected void click(Locator locator, String locatorDescription) {
        locator.click();
        logger.info("Clicked on: {}", locatorDescription);
    }

    @Step("Fill field '{locatorDescription}' with value: {value}")
    protected void fill(Locator locator, String value, String locatorDescription) {
        locator.fill(value);
        logger.info("Filled field '{}' with value: {}", locatorDescription, value);
    }

    @Step("Get text from element: {locatorDescription}")
    protected String getText(Locator locator, String locatorDescription) {
        String text = locator.textContent();
        logger.info("Text from '{}': {}", locatorDescription, text);
        return text;
    }

    @Step("Check if element is visible: {locatorDescription}")
    protected boolean isVisible(Locator locator, String locatorDescription) {
        boolean visible = locator.isVisible();
        logger.info("Element '{}' visibility: {}", locatorDescription, visible);
        return visible;
    }

    @Step("Check if element is enabled: {locatorDescription}")
    protected boolean isEnabled(Locator locator, String locatorDescription) {
        boolean enabled = locator.isEnabled();
        logger.info("Element '{}' enabled: {}", locatorDescription, enabled);
        return enabled;
    }

    @Step("Wait for element to be visible: {locatorDescription}")
    protected void waitForVisible(Locator locator, String locatorDescription) {
        locator.waitFor();
        logger.info("Element '{}' became visible", locatorDescription);
    }

    @Step("Select option '{value}' from dropdown: {locatorDescription}")
    protected void selectOption(Locator locator, String value, String locatorDescription) {
        locator.selectOption(value);
        logger.info("Selected option '{}' from dropdown '{}'", value, locatorDescription);
    }

    @Step("Get attribute '{attribute}' from element: {locatorDescription}")
    protected String getAttribute(Locator locator, String attribute, String locatorDescription) {
        String value = locator.getAttribute(attribute);
        logger.info("Attribute '{}' from element '{}': {}", attribute, locatorDescription, value);
        return value;
    }

    @Step("Scroll element into view: {locatorDescription}")
    protected void scrollIntoView(Locator locator, String locatorDescription) {
        locator.scrollIntoViewIfNeeded();
        logger.info("Scrolled element '{}' into view", locatorDescription);
    }
}
