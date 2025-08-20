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
            case "webkit" -> playwright.webkit();
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
        
        // Use Google Chrome for visible mode on macOS (better compatibility)
        // Keep Chromium for headless mode (faster, lighter)
        if (browserName.toLowerCase().equals("chromium") && 
            !headlessMode && 
            System.getProperty("os.name").toLowerCase().contains("mac")) {
            
            String chromePath = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
            if (java.nio.file.Files.exists(Paths.get(chromePath))) {
                options.setExecutablePath(Paths.get(chromePath));
                logger.info("Using Google Chrome for visible mode on macOS (better compatibility)");
            } else {
                logger.warn("Google Chrome not found at {}. Falling back to Chromium.", chromePath);
            }
        }
        
        // Add macOS specific fixes for visible mode (headless=false)
        if (!headlessMode && System.getProperty("os.name").toLowerCase().contains("mac")) {
            List<String> args = new ArrayList<>(Arrays.asList(
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
            options.setArgs(args);
            logger.info("Applied enhanced macOS compatibility arguments for visible mode");
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
            page.close();
            pageThreadLocal.remove();
            logger.info("Page closed");
        }
    }

    public static void closeContext() {
        BrowserContext context = contextThreadLocal.get();
        if (context != null) {
            context.close();
            contextThreadLocal.remove();
            logger.info("Browser context closed");
        }
    }

    public static void closeBrowser() {
        Browser browser = browserThreadLocal.get();
        if (browser != null) {
            browser.close();
            browserThreadLocal.remove();
            logger.info("Browser closed");
        }
    }

    public static void closePlaywright() {
        if (playwright != null) {
            playwright.close();
            playwright = null;
            logger.info("Playwright closed");
        }
    }

    private static boolean getVideoMode() {
        return !config.videoMode().equalsIgnoreCase("OFF");
    }

    private static boolean getTraceMode() {
        return !config.traceMode().equalsIgnoreCase("OFF");
    }
}
