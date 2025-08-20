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
        
        // Attach existing screenshots for this test (captured by BaseTest)
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
                logger.info("Looking for screenshots for test: {}", testName);
                
                // Extract class name from testName (e.g., "QuickScreenshotDemo.QUICK DEMO: ..." -> "QuickScreenshotDemo")
                String className = testName.split("\\.")[0];
                
                Files.list(screenshotsDir)
                    .filter(path -> {
                        String filename = path.getFileName().toString();
                        logger.info("Checking screenshot file: {}", filename);
                        // Match files that start with the class name and contain "DEMO"
                        return filename.startsWith(className) && filename.contains("DEMO");
                    })
                    .forEach(path -> {
                        try {
                            byte[] screenshot = Files.readAllBytes(path);
                            Allure.addAttachment("Failure Screenshot", "image/png", 
                                new ByteArrayInputStream(screenshot), "png");
                            logger.info("✅ Screenshot attached to Allure: {}", path.getFileName());
                        } catch (Exception e) {
                            logger.error("Failed to attach screenshot: {}", e.getMessage());
                        }
                    });
            } else {
                logger.warn("Screenshots directory does not exist: {}", screenshotsDir);
            }
        } catch (Exception e) {
            logger.error("Failed to search for existing screenshots: {}", e.getMessage());
        }
    }
}
