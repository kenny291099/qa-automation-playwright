package com.saucedemo.tests;

import com.saucedemo.config.TestConfig;
import com.saucedemo.utils.BrowserManager;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

@ExtendWith(TestResultListener.class)
public abstract class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static final TestConfig config = ConfigFactory.create(TestConfig.class);

    @BeforeAll
    static void setUpAll() {
        logger.info("Setting up test environment");
        createDirectories();
        BrowserManager.initializePlaywright();
    }

    @BeforeEach
    void setUp() {
        logger.info("Starting test: {}", getTestName());
        BrowserManager.createBrowser();
        BrowserManager.createContext();
        BrowserManager.createPage();
        
        // Navigate to base URL
        BrowserManager.getPage().navigate(config.baseUrl());
        logger.info("Navigated to: {}", config.baseUrl());
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        logger.info("Tearing down test: {}", getTestName());
        
        BrowserManager.closePage();
        BrowserManager.closeContext();
        BrowserManager.closeBrowser();
    }

    @AfterAll
    static void tearDownAll() {
        logger.info("Tearing down test environment");
        BrowserManager.closePlaywright();
    }

    protected String getTestName() {
        return this.getClass().getSimpleName();
    }

    private static void createDirectories() {
        try {
            Files.createDirectories(Paths.get("test-results/screenshots"));
            Files.createDirectories(Paths.get("test-results/videos"));
            Files.createDirectories(Paths.get("test-results/traces"));
            logger.info("Test result directories created");
        } catch (Exception e) {
            logger.error("Failed to create directories: {}", e.getMessage());
        }
    }
}
