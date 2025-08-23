package com.saucedemo.tests;

import com.saucedemo.utils.BrowserManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TestResultListener implements TestWatcher {
    private static final Logger logger = LoggerFactory.getLogger(TestResultListener.class);

    @Override
    public void testSuccessful(ExtensionContext context) {
        String testName = getTestName(context);
        logger.info("✅ Test PASSED: {}", testName);
        
        // Take screenshot on success if configured
        if (shouldTakeScreenshotOnSuccess()) {
            String screenshotName = testName + "_success_" + getTimestamp();
            BrowserManager.takeScreenshot(screenshotName);
            attachScreenshotToAllure(screenshotName, "Success Screenshot");
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = getTestName(context);
        logger.error("❌ Test FAILED: {} - Error: {}", testName, cause.getMessage());
        
        // Take screenshot immediately on failure and attach to Allure
        // This runs BEFORE BaseTest.tearDown(), so browser should still be available
        takeAndAttachScreenshot(testName);
        
        // Also check for any existing screenshots (for demo tests)
        attachExistingScreenshots(testName);
        
        // Save trace on failure
        try {
            String traceName = testName + "_failure_" + getTimestamp();
            BrowserManager.saveTrace(traceName);
        } catch (Exception e) {
            logger.warn("Could not save trace: {}", e.getMessage());
        }
        
        // Attach error details to Allure
        Allure.addAttachment("Error Details", "text/plain", 
            "Test: " + testName + "\n" +
            "Error: " + cause.getMessage() + "\n" +
            "Stack Trace: " + getStackTrace(cause));
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        String testName = getTestName(context);
        logger.warn("⚠️ Test ABORTED: {} - Reason: {}", testName, cause.getMessage());
        
        // Take screenshot on abort
        String screenshotName = testName + "_aborted_" + getTimestamp();
        BrowserManager.takeScreenshot(screenshotName);
        attachScreenshotToAllure(screenshotName, "Aborted Screenshot");
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        String testName = getTestName(context);
        String reasonText = reason.orElse("No reason provided");
        logger.info("⏭️ Test DISABLED: {} - Reason: {}", testName, reasonText);
    }

    private String getTestName(ExtensionContext context) {
        return context.getTestClass().map(Class::getSimpleName).orElse("Unknown") + 
               "." + context.getDisplayName();
    }

    private String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    private void takeAndAttachScreenshot(String testName) {
        try {
            // Check if browser/page is still available
            if (BrowserManager.getPage() != null && !BrowserManager.getPage().isClosed()) {
                // Primary method: Take screenshot directly and attach to Allure
                byte[] screenshotBytes = BrowserManager.getPage().screenshot();
                Allure.addAttachment("Failure Screenshot", "image/png", 
                    new ByteArrayInputStream(screenshotBytes), "png");
                logger.info("✅ Failure screenshot captured and attached to Allure");
                
                // Optional: Also save to file for debugging
                try {
                    String screenshotName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_failure_" + getTimestamp();
                    BrowserManager.takeScreenshot(screenshotName);
                    logger.debug("Screenshot also saved to file: {}", screenshotName);
                } catch (Exception fileEx) {
                    logger.debug("Could not save screenshot to file: {}", fileEx.getMessage());
                }
                return; // Successfully captured screenshot
            }
            
            logger.warn("Browser/page not available for direct screenshot capture, trying fallback methods");
            
        } catch (Exception e) {
            logger.error("Failed to take failure screenshot: {}", e.getMessage());
        }
        
        // Fallback 1: Try to find recently saved screenshots from BaseTest.tearDown()
        try {
            String cleanTestName = testName.replaceAll("[^a-zA-Z0-9]", "_");
            Path screenshotsDir = Paths.get("test-results/screenshots");
            
            if (Files.exists(screenshotsDir)) {
                // Look for recent screenshots matching this test
                Optional<Path> recentScreenshot = Files.list(screenshotsDir)
                    .filter(path -> {
                        String filename = path.getFileName().toString();
                        return filename.contains(cleanTestName) && 
                               filename.endsWith(".png") &&
                               isRecentFile(path, 10); // within last 10 seconds
                    })
                    .findFirst();
                
                if (recentScreenshot.isPresent()) {
                    byte[] screenshot = Files.readAllBytes(recentScreenshot.get());
                    Allure.addAttachment("Failure Screenshot", "image/png", 
                        new ByteArrayInputStream(screenshot), "png");
                    logger.info("✅ Recent screenshot attached to Allure: {}", recentScreenshot.get().getFileName());
                    return;
                }
            }
        } catch (Exception fallbackEx) {
            logger.debug("Could not find recent screenshots: {}", fallbackEx.getMessage());
        }
        
        // Fallback 2: Save a text attachment explaining the issue
        logger.warn("No screenshot could be captured or found for failed test: {}", testName);
        Allure.addAttachment("Screenshot Status", "text/plain", 
            "Screenshot could not be captured for this test failure.\n" +
            "This may be due to browser being closed before screenshot capture.\n" +
            "Test: " + testName + "\n" +
            "Timestamp: " + getTimestamp());
    }
    
    private boolean isRecentFile(Path file, int secondsAgo) {
        try {
            long fileTime = Files.getLastModifiedTime(file).toMillis();
            long currentTime = System.currentTimeMillis();
            return (currentTime - fileTime) < (secondsAgo * 1000);
        } catch (Exception e) {
            return false;
        }
    }

    private void attachScreenshotToAllure(String screenshotName, String description) {
        try {
            Path screenshotPath = Paths.get("test-results/screenshots/" + screenshotName + ".png");
            if (Files.exists(screenshotPath)) {
                byte[] screenshot = Files.readAllBytes(screenshotPath);
                Allure.addAttachment(description, "image/png", 
                    new ByteArrayInputStream(screenshot), "png");
                logger.info("Screenshot attached to Allure report: {}", screenshotName);
            }
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to Allure: {}", e.getMessage());
        }
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private boolean shouldTakeScreenshotOnSuccess() {
        return System.getProperty("screenshot.mode", "ON_FAILURE").equalsIgnoreCase("ALWAYS");
    }
    
    private void attachExistingScreenshots(String testName) {
        try {
            Path screenshotsDir = Paths.get("test-results/screenshots");
            if (Files.exists(screenshotsDir)) {
                logger.debug("Looking for existing screenshots for test: {}", testName);
                
                // Extract class name from testName (e.g., "LoginTest.testSuccessfulLogin()" -> "LoginTest")
                String className = testName.split("\\.")[0];
                
                Files.list(screenshotsDir)
                    .filter(path -> {
                        String filename = path.getFileName().toString();
                        // Look for screenshots that match the class name or contain DEMO
                        return (filename.startsWith(className) && filename.contains("DEMO")) ||
                               filename.contains(className);
                    })
                    .forEach(path -> {
                        try {
                            byte[] screenshot = Files.readAllBytes(path);
                            Allure.addAttachment("Additional Screenshot", "image/png", 
                                new ByteArrayInputStream(screenshot), "png");
                            logger.info("✅ Additional screenshot attached: {}", path.getFileName());
                        } catch (Exception e) {
                            logger.error("Failed to attach additional screenshot: {}", e.getMessage());
                        }
                    });
            }
        } catch (Exception e) {
            logger.debug("No additional screenshots found or error occurred: {}", e.getMessage());
        }
    }
}
