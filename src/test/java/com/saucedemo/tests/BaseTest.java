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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(TestResultListener.class)
public abstract class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static final TestConfig config = ConfigFactory.create(TestConfig.class);

    @BeforeAll
    static void setUpAll() {
        logger.info("Setting up test environment");
        cleanPreviousScreenshots();
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
        
        // Take screenshot before closing if test failed
        // This runs after test but before TestResultListener
        try {
            if (BrowserManager.getPage() != null && testInfo.getDisplayName().contains("DEMO")) {
                String screenshotName = getTestName() + "_" + testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_") + "_" + getTimestamp();
                BrowserManager.takeScreenshot(screenshotName);
                logger.info("Screenshot taken: {}", screenshotName);
                // Note: Screenshot file saved for TestResultListener to attach to Allure
            }
        } catch (Exception e) {
            logger.warn("Could not take screenshot in tearDown: {}", e.getMessage());
        }
        
        BrowserManager.closePage();
        BrowserManager.closeContext();
        BrowserManager.closeBrowser();
    }

    @AfterAll
    static void tearDownAll() {
        logger.info("Tearing down test environment");
        BrowserManager.closePlaywright();
        
        // Clean screenshots after test run if configured
        if (shouldCleanScreenshotsAfterRun()) {
            cleanAllScreenshots();
        }
    }

    protected String getTestName() {
        return this.getClass().getSimpleName();
    }
    
    protected String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
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
    
    private static void cleanPreviousScreenshots() {
        try {
            // Clean screenshot files
            Path screenshotsDir = Paths.get("test-results/screenshots");
            if (Files.exists(screenshotsDir)) {
                Files.list(screenshotsDir)
                    .filter(path -> path.toString().endsWith(".png"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (Exception e) {
                            logger.warn("Could not delete screenshot: {}", path.getFileName());
                        }
                    });
                logger.info("🧹 Previous screenshots cleaned");
            }
            
            // Clean previous Allure results
            cleanAllureResults();
            
        } catch (Exception e) {
            logger.warn("Failed to clean previous screenshots: {}", e.getMessage());
        }
    }
    
    private static void cleanAllScreenshots() {
        try {
            Path screenshotsDir = Paths.get("test-results/screenshots");
            if (Files.exists(screenshotsDir)) {
                Files.list(screenshotsDir)
                    .filter(path -> path.toString().endsWith(".png"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            logger.debug("Cleaned screenshot: {}", path.getFileName());
                        } catch (Exception e) {
                            logger.warn("Could not delete screenshot: {}", path.getFileName());
                        }
                    });
                logger.info("🧹 All screenshots cleaned after test run");
            }
        } catch (Exception e) {
            logger.warn("Failed to clean screenshots after test run: {}", e.getMessage());
        }
    }
    
    private static boolean shouldCleanScreenshotsAfterRun() {
        // Clean screenshots after run by default, unless explicitly disabled
        return !System.getProperty("screenshot.cleanup", "true").equalsIgnoreCase("false");
    }
    
    private static void cleanAllureResults() {
        try {
            Path allureResultsDir = Paths.get("target/allure-results");
            if (Files.exists(allureResultsDir)) {
                Files.list(allureResultsDir)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (Exception e) {
                            logger.warn("Could not delete allure result: {}", path.getFileName());
                        }
                    });
                logger.info("🧹 Previous Allure results cleaned");
            }
        } catch (Exception e) {
            logger.warn("Failed to clean Allure results: {}", e.getMessage());
        }
    }
    

}
