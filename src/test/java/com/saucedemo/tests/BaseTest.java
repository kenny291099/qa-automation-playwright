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
        // Log JVM instance details to verify same JVM execution
        String jvmName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        String pid = jvmName.split("@")[0];
        logger.info("Setting up test environment in JVM PID: {} ({})", pid, jvmName);
        logger.info("Test class: {}", getTestClassName());
        
        cleanOncePerMavenRun();
        createDirectories();
        BrowserManager.initializePlaywright();
    }

    @BeforeEach
    void setUp() {
        logger.info("Starting test: {}", getTestName());
        
        // Ensure Playwright is initialized (in case it was closed in previous test teardown)
        BrowserManager.initializePlaywright();
        
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
        
        // Enhanced cleanup: Ensure complete browser closure after each test
        // This allows running entire test classes in visible mode without hanging
        try {
            logger.debug("Starting enhanced browser cleanup for test: {}", testInfo.getDisplayName());
            
            // Step 1: Force cleanup with timeout
            BrowserManager.forceCleanupWithTimeout();
            
            // Step 2: Ensure Playwright instance is ready for next test
            // Close and reinitialize Playwright to prevent browser process accumulation
            BrowserManager.closePlaywright();
            
            logger.info("✅ Browser completely closed for test: {}", testInfo.getDisplayName());
            
        } catch (Exception e) {
            logger.error("Error during enhanced browser cleanup: {}", e.getMessage());
            // Force cleanup even if there's an error
            try {
                BrowserManager.closePlaywright();
            } catch (Exception ex) {
                logger.warn("Final cleanup attempt failed: {}", ex.getMessage());
            }
        }
    }

    @AfterAll
    static void tearDownAll() {
        logger.info("Tearing down test environment");
        
        // No need to close Playwright here - it's closed after each individual test
        // This ensures better isolation and prevents browser process accumulation
        
        // Clean screenshots after test run if configured
        if (shouldCleanScreenshotsAfterRun()) {
            cleanAllScreenshots();
        }
        
        // DO NOT clean Allure results here - they are preserved across all test classes
        // Only the first test class in a Maven run cleans previous results
        // DO NOT delete cleanup marker file here - let Maven clean handle it
        
        logger.info("Test class '{}' completed - Enhanced cleanup ensures no browser processes remain", getTestClassName());
    }

    protected String getTestName() {
        return this.getClass().getSimpleName();
    }
    
    protected static String getTestClassName() {
        // Get the actual test class name from the stack trace
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            if (element.getClassName().contains("Test") && !element.getClassName().equals(BaseTest.class.getName())) {
                return element.getClassName().substring(element.getClassName().lastIndexOf('.') + 1);
            }
        }
        return "UnknownTestClass";
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
    
    // File-based marker to track if cleanup has been done in this JVM session
    // Uses JVM start time and ID for reliable first-vs-subsequent test class detection
    private static final Path CLEANUP_MARKER_FILE = Paths.get("target/.cleanup-performed");
    
    private static void cleanOncePerMavenRun() {
        // Use JVM start time as a reliable indicator of current test session
        long jvmStartTime = java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
        String jvmId = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        
        logger.debug("Current JVM ID: {}, Start time: {}", jvmId, jvmStartTime);
        
        // Check if cleanup marker exists and is from current JVM session
        if (Files.exists(CLEANUP_MARKER_FILE)) {
            try {
                String markerContent = Files.readString(CLEANUP_MARKER_FILE);
                String[] parts = markerContent.trim().split(":");
                
                if (parts.length == 2) {
                    long markerJvmStartTime = Long.parseLong(parts[0]);
                    String markerJvmId = parts[1];
                    
                    // If marker is from current JVM session, this is a subsequent test class
                    if (markerJvmStartTime == jvmStartTime && markerJvmId.equals(jvmId)) {
                        // This is a subsequent test class - preserve all existing results
                        long allureResultCount = countExistingAllureResults();
                        long screenshotCount = countExistingScreenshots();
                        
                        logger.info("SUBSEQUENT TEST CLASS - Preserving existing results from current Maven run:");
                        logger.info("  - Allure results: {} files", allureResultCount);
                        logger.info("  - Screenshots: {} files", screenshotCount);
                        logger.info("  - Test class: {}", getTestClassName());
                        logger.info("  - JVM session: {} (started: {})", jvmId, jvmStartTime);
                        
                        return; // Skip cleanup - preserve all results from current Maven run
                    }
                }
                
                // Marker is from different JVM session, so cleanup and create new marker
                logger.info("FIRST TEST CLASS of NEW MAVEN RUN - previous marker from different JVM session, performing cleanup");
                
            } catch (Exception e) {
                logger.warn("Could not read cleanup marker file, treating as new run: {}", e.getMessage());
                // Continue with cleanup
            }
        } else {
            logger.info("FIRST TEST CLASS of NEW MAVEN RUN - no previous marker found, performing cleanup");
        }
        
        // Create marker file with current JVM session info and perform cleanup
        try {
            Files.createDirectories(CLEANUP_MARKER_FILE.getParent());
            String markerContent = jvmStartTime + ":" + jvmId;
            Files.writeString(CLEANUP_MARKER_FILE, markerContent);
            logger.info("Created cleanup marker for current JVM session: {} (started: {})", jvmId, jvmStartTime);
        } catch (Exception e) {
            logger.warn("Could not create cleanup marker file: {}", e.getMessage());
            // Continue with cleanup anyway
        }
        
        // Perform cleanup for first test class only
        performFirstTestClassCleanup();
    }
    
    private static void performFirstTestClassCleanup() {
        logger.info("PERFORMING FIRST TEST CLASS CLEANUP:");
        
        try {
            // Count existing items before cleanup
            long existingAllureResults = countExistingAllureResults();
            long existingScreenshots = countExistingScreenshots();
            
            logger.info("  - Found {} existing Allure results to clean", existingAllureResults);
            logger.info("  - Found {} existing screenshots to clean", existingScreenshots);
            
            // Clean all screenshot files
            cleanAllScreenshotsInternal();
            
            // Clean all Allure results
            cleanAllureResults();
            
            // Verify cleanup
            long remainingAllureResults = countExistingAllureResults();
            long remainingScreenshots = countExistingScreenshots();
            
            logger.info("CLEANUP COMPLETED:");
            logger.info("  - Allure results cleaned: {} -> {}", existingAllureResults, remainingAllureResults);
            logger.info("  - Screenshots cleaned: {} -> {}", existingScreenshots, remainingScreenshots);
            logger.info("  - Test class: {}", getTestClassName());
            
        } catch (Exception e) {
            logger.warn("Failed to perform first test class cleanup: {}", e.getMessage());
        }
    }
    
    private static long countExistingAllureResults() {
        try {
            Path allureResultsDir = Paths.get("target/allure-results");
            if (Files.exists(allureResultsDir)) {
                return Files.list(allureResultsDir)
                    .filter(path -> path.toString().endsWith("-result.json") || 
                                   path.toString().endsWith("-container.json") ||
                                   path.toString().endsWith("-attachment"))
                    .count();
            }
        } catch (Exception e) {
            logger.debug("Could not count existing Allure results: {}", e.getMessage());
        }
        return 0;
    }
    
    private static long countExistingScreenshots() {
        try {
            Path screenshotsDir = Paths.get("test-results/screenshots");
            if (Files.exists(screenshotsDir)) {
                return Files.list(screenshotsDir)
                    .filter(path -> path.toString().endsWith(".png"))
                    .count();
            }
        } catch (Exception e) {
            logger.debug("Could not count existing screenshots: {}", e.getMessage());
        }
        return 0;
    }
    
    private static void cleanAllScreenshotsInternal() {
        try {
            Path screenshotsDir = Paths.get("test-results/screenshots");
            if (Files.exists(screenshotsDir)) {
                long deletedCount = 0;
                try {
                    deletedCount = Files.list(screenshotsDir)
                        .filter(path -> path.toString().endsWith(".png"))
                        .mapToLong(path -> {
                            try {
                                Files.delete(path);
                                logger.debug("Deleted screenshot: {}", path.getFileName());
                                return 1;
                            } catch (Exception e) {
                                logger.debug("Could not delete screenshot: {}", path.getFileName());
                                return 0;
                            }
                        })
                        .sum();
                    logger.info("Deleted {} previous screenshots", deletedCount);
                } catch (Exception e) {
                    logger.warn("Error listing screenshot files: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to clean screenshots: {}", e.getMessage());
        }
    }
    
    private static void cleanAllScreenshots() {
        try {
            Path screenshotsDir = Paths.get("test-results/screenshots");
            if (Files.exists(screenshotsDir)) {
                try {
                    Files.list(screenshotsDir)
                        .filter(path -> path.toString().endsWith(".png"))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                logger.debug("Cleaned screenshot: {}", path.getFileName());
                            } catch (Exception e) {
                                logger.debug("Could not delete screenshot: {}", path.getFileName());
                            }
                        });
                    logger.info("All screenshots cleaned after test run");
                } catch (Exception e) {
                    logger.warn("Error listing screenshots for cleanup: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to clean screenshots after test run: {}", e.getMessage());
        }
    }
    
    private static boolean shouldCleanScreenshotsAfterRun() {
        // Clean screenshots after run by default, unless explicitly disabled
        return !System.getProperty("screenshot.cleanup", "true").equalsIgnoreCase("false");
    }
    
    // Removed shouldCleanAllureAfterRun() - we never clean Allure results during test run
    
    private static void cleanAllureResults() {
        try {
            Path allureResultsDir = Paths.get("target/allure-results");
            if (Files.exists(allureResultsDir)) {
                try {
                    long deletedCount = Files.list(allureResultsDir)
                        .mapToLong(path -> {
                            try {
                                Files.delete(path);
                                logger.debug("Deleted Allure file: {}", path.getFileName());
                                return 1;
                            } catch (Exception e) {
                                logger.debug("Could not delete Allure file: {}", path.getFileName());
                                return 0;
                            }
                        })
                        .sum();
                    logger.info("Deleted {} previous Allure result files", deletedCount);
                } catch (Exception e) {
                    logger.warn("Error listing Allure results for cleanup: {}", e.getMessage());
                }
            } else {
                logger.debug("Allure results directory does not exist, nothing to clean");
            }
        } catch (Exception e) {
            logger.warn("Failed to clean Allure results: {}", e.getMessage());
        }
    }

    // Removed cleanupMarkerFile() - Maven clean handles marker file deletion

}
