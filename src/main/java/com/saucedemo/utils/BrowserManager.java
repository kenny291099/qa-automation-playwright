package com.saucedemo.utils;

import com.microsoft.playwright.*;
import com.saucedemo.config.TestConfig;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BrowserManager {
    private static final Logger logger = LoggerFactory.getLogger(BrowserManager.class);
    private static final TestConfig config = ConfigFactory.create(TestConfig.class);
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private static Playwright playwright;

    public static void initializePlaywright() {
        if (playwright == null) {
            playwright = Playwright.create();
            logger.info("Playwright initialized");
        }
    }

    public static void createBrowser() {
        initializePlaywright();
        
        String browserName = System.getProperty("browser", config.browser());
        BrowserType browserType = switch (browserName.toLowerCase()) {
            case "firefox" -> playwright.firefox();
            case "webkit", "safari" -> playwright.webkit();
            case "chrome", "chromium" -> playwright.chromium();
            default -> playwright.chromium();
        };

        // Check system property for headless mode override
        boolean headlessMode = Boolean.parseBoolean(
            System.getProperty("headless", String.valueOf(config.headless())));
        
        int slowMotion = Integer.parseInt(
            System.getProperty("slow.mo", String.valueOf(config.slowMo())));
        
        // Create launch options
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(headlessMode)
                .setSlowMo(slowMotion);
        
        // Browser-specific optimizations for visible mode on macOS
        if (!headlessMode && System.getProperty("os.name").toLowerCase().contains("mac")) {
            
            // Use Google Chrome for Chromium/Chrome in visible mode (better compatibility)
            if (browserName.toLowerCase().equals("chromium") || browserName.toLowerCase().equals("chrome")) {
                String chromePath = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
                if (java.nio.file.Files.exists(Paths.get(chromePath))) {
                    options.setExecutablePath(Paths.get(chromePath));
                    logger.info("Using Google Chrome for visible mode on macOS (better compatibility)");
                } else {
                    logger.warn("Google Chrome not found at {}. Falling back to Chromium.", chromePath);
                }
            }
            
            // For WebKit/Safari, don't use Safari executable - use Playwright's WebKit engine
            // Safari executable doesn't work well with Playwright automation
            else if (browserName.toLowerCase().equals("webkit") || browserName.toLowerCase().equals("safari")) {
                logger.info("Using Playwright WebKit engine (Safari-compatible) for visible mode");
            }
        }
        
        // Add macOS specific fixes for visible mode (headless=false)
        if (!headlessMode && System.getProperty("os.name").toLowerCase().contains("mac")) {
            List<String> args = new ArrayList<>();
            
            // Browser-specific arguments for stability
            if (browserName.toLowerCase().equals("chromium") || browserName.toLowerCase().equals("chrome")) {
                args.addAll(Arrays.asList(
                    "--no-sandbox",
                    "--disable-dev-shm-usage", 
                    "--disable-web-security",
                    "--disable-features=VizDisplayCompositor",
                    "--disable-background-timer-throttling",
                    "--disable-backgrounding-occluded-windows",
                    "--disable-renderer-backgrounding",
                    "--disable-field-trial-config",
                    "--disable-ipc-flooding-protection",
                    "--no-first-run",
                    "--no-default-browser-check", 
                    "--disable-default-apps",
                    "--disable-popup-blocking",
                    "--disable-translate",
                    "--disable-extensions",
                    "--disable-sync",
                    "--disable-background-mode",
                    "--remote-debugging-port=9222"
                ));
                logger.info("Applied Chrome/Chromium macOS compatibility arguments for visible mode");
            }
            else if (browserName.toLowerCase().equals("webkit") || browserName.toLowerCase().equals("safari")) {
                // Use minimal arguments for WebKit - it's more sensitive to unknown flags
                args.addAll(Arrays.asList(
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-web-security"
                ));
                logger.info("Applied WebKit/Safari minimal compatibility arguments for visible mode");
            }
            else {
                // Firefox or other browsers - minimal args
                args.addAll(Arrays.asList(
                    "--no-sandbox",
                    "--disable-dev-shm-usage"
                ));
                logger.info("Applied basic macOS compatibility arguments for visible mode");
            }
            
            if (!args.isEmpty()) {
                options.setArgs(args);
            }
        }
        
        Browser browser = browserType.launch(options);
        
        browserThreadLocal.set(browser);
        logger.info("Browser {} created with headless: {} (slow motion: {}ms)", 
                browserName, headlessMode, slowMotion);
    }

    public static void createContext() {
        Browser browser = browserThreadLocal.get();
        if (browser == null) {
            throw new IllegalStateException("Browser not initialized. Call createBrowser() first.");
        }

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setRecordVideoDir(getVideoMode() ? Paths.get("test-results/videos") : null)
                .setRecordVideoSize(1920, 1080));

        if (getTraceMode()) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }

        contextThreadLocal.set(context);
        logger.info("Browser context created");
    }

    public static void createPage() {
        BrowserContext context = contextThreadLocal.get();
        if (context == null) {
            throw new IllegalStateException("Browser context not initialized. Call createContext() first.");
        }

        Page page = context.newPage();
        page.setDefaultTimeout(config.timeout());
        pageThreadLocal.set(page);
        logger.info("Page created with timeout: {}ms", config.timeout());
    }

    public static Page getPage() {
        Page page = pageThreadLocal.get();
        if (page == null) {
            throw new IllegalStateException("Page not initialized. Call createPage() first.");
        }
        return page;
    }

    public static BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    public static Browser getBrowser() {
        return browserThreadLocal.get();
    }

    public static void takeScreenshot(String name) {
        Page page = getPage();
        if (page != null) {
            try {
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get("test-results/screenshots/" + name + ".png"))
                        .setFullPage(true));
                logger.info("Screenshot taken: {}", name);
            } catch (Exception e) {
                logger.error("Failed to take screenshot: {}", e.getMessage());
            }
        }
    }

    public static void saveTrace(String name) {
        BrowserContext context = getContext();
        if (context != null && getTraceMode()) {
            try {
                context.tracing().stop(new Tracing.StopOptions()
                        .setPath(Paths.get("test-results/traces/" + name + ".zip")));
                logger.info("Trace saved: {}", name);
            } catch (Exception e) {
                logger.error("Failed to save trace: {}", e.getMessage());
            }
        }
    }

    public static void closePage() {
        Page page = pageThreadLocal.get();
        if (page != null) {
            try {
                if (!page.isClosed()) {
                    page.close();
                }
                logger.info("Page closed");
            } catch (Exception e) {
                logger.warn("Error closing page: {}", e.getMessage());
            } finally {
                pageThreadLocal.remove();
            }
        }
    }

    public static void closeContext() {
        BrowserContext context = contextThreadLocal.get();
        if (context != null) {
            try {
                // Stop tracing first if enabled
                if (getTraceMode()) {
                    try {
                        context.tracing().stop();
                    } catch (Exception e) {
                        logger.debug("Error stopping trace: {}", e.getMessage());
                    }
                }
                context.close();
                logger.info("Browser context closed");
            } catch (Exception e) {
                logger.warn("Error closing browser context: {}", e.getMessage());
            } finally {
                contextThreadLocal.remove();
            }
        }
    }

    public static void closeBrowser() {
        Browser browser = browserThreadLocal.get();
        if (browser != null) {
            try {
                if (browser.isConnected()) {
                    // Force close all contexts first in visible mode
                    try {
                        for (BrowserContext context : browser.contexts()) {
                            if (!context.equals(contextThreadLocal.get())) {
                                context.close();
                            }
                        }
                    } catch (Exception e) {
                        logger.debug("Error closing additional contexts: {}", e.getMessage());
                    }
                    
                    browser.close();
                }
                logger.info("Browser closed");
            } catch (Exception e) {
                logger.warn("Error closing browser: {}", e.getMessage());
            } finally {
                browserThreadLocal.remove();
            }
        }
    }

    public static void closePlaywright() {
        if (playwright != null) {
            try {
                playwright.close();
                logger.info("Playwright closed");
            } catch (Exception e) {
                logger.warn("Error closing Playwright: {}", e.getMessage());
            } finally {
                playwright = null;
            }
        }
    }

    /**
     * Force cleanup all browser resources with error isolation
     * Each cleanup step is isolated to prevent cascading failures
     */
    public static void forceCleanup() {
        logger.debug("Starting force cleanup of browser resources");
        
        // Clean up page
        try {
            closePage();
        } catch (Exception e) {
            logger.warn("Force cleanup - page error: {}", e.getMessage());
            pageThreadLocal.remove();
        }
        
        // Clean up context
        try {
            closeContext();
        } catch (Exception e) {
            logger.warn("Force cleanup - context error: {}", e.getMessage());
            contextThreadLocal.remove();
        }
        
        // Clean up browser
        try {
            closeBrowser();
        } catch (Exception e) {
            logger.warn("Force cleanup - browser error: {}", e.getMessage());
            browserThreadLocal.remove();
        }
        
        logger.debug("Force cleanup completed");
    }

    /**
     * Force cleanup with timeout protection to prevent hanging
     * Uses ExecutorService to run cleanup operations with timeout
     */
    public static void forceCleanupWithTimeout() {
        logger.debug("Starting force cleanup with timeout protection");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        try {
            Future<?> future = executor.submit(() -> {
                try {
                    forceCleanup();
                } catch (Exception e) {
                    logger.warn("Exception during timeout cleanup: {}", e.getMessage());
                }
            });
            
            // Use shorter timeout for visible browsers, but give WebKit/Safari a bit more time
            // WebKit can be slower to cleanup than Chromium-based browsers
            int timeoutSeconds = 5; // Default
            if (System.getProperty("headless", "true").equals("false")) {
                String browserName = System.getProperty("browser", "chromium").toLowerCase();
                if (browserName.equals("webkit") || browserName.equals("safari")) {
                    timeoutSeconds = 4; // WebKit needs slightly more time
                } else {
                    timeoutSeconds = 3; // Chrome/Firefox are faster
                }
            }
            future.get(timeoutSeconds, TimeUnit.SECONDS);
            logger.debug("Timeout cleanup completed successfully in {}s", timeoutSeconds);
            
        } catch (Exception e) {
            logger.warn("Cleanup timed out or failed after timeout: {}", e.getMessage());
            // Force ThreadLocal cleanup even if timeout occurred
            pageThreadLocal.remove();
            contextThreadLocal.remove();
            browserThreadLocal.remove();
            logger.info("ThreadLocal variables forcefully cleared due to timeout");
        } finally {
            executor.shutdownNow();
        }
    }

    private static boolean getVideoMode() {
        return !config.videoMode().equalsIgnoreCase("OFF");
    }

    private static boolean getTraceMode() {
        return !config.traceMode().equalsIgnoreCase("OFF");
    }
}
