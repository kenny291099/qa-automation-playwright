# 📁 Project Structure

```text
qa-automation-playwright/
├── allure-report/                 # Generated Allure reports
├── docs/                          # Project documentation
│   ├── ci-cd.md                   # CI/CD integration guide
│   ├── configuration.md           # Configuration documentation
│   ├── project-structure.md       # This file - project structure
│   ├── test-coverage.md           # Test coverage documentation
│   └── troubleshooting.md         # Troubleshooting guide
├── logs/                          # Test execution logs
├── src/
│   ├── main/java/com/saucedemo/   # Example implementation (Sauce Demo)
│   │   ├── config/
│   │   │   └── TestConfig.java    # Configuration interface
│   │   ├── pages/                 # Page Object Model classes
│   │   │   ├── BasePage.java      # Base page with common functionality
│   │   │   ├── CartPage.java      # Shopping cart page
│   │   │   ├── CheckoutPage.java  # Checkout flow pages
│   │   │   ├── InventoryPage.java # Product listing page
│   │   │   ├── LoginPage.java     # Login page implementation
│   │   │   └── ProductDetailsPage.java # Product detail page
│   │   └── utils/
│   │       └── BrowserManager.java # Browser lifecycle management
│   └── test/
│       ├── java/com/saucedemo/tests/
│       │   ├── BaseTest.java      # Base test class with setup/teardown
│       │   ├── CartPageTest.java  # Shopping cart functionality tests
│       │   ├── CheckoutPageTest.java # Checkout process tests
│       │   ├── E2ETest.java       # End-to-end workflow tests
│       │   ├── FailingTestsForScreenshotDemo.java # Demo tests for screenshot capture
│       │   ├── InventoryTest.java # Product management tests
│       │   ├── LoginTest.java     # Login functionality tests
│       │   ├── NavigationTest.java # Navigation and routing tests
│       │   ├── ProductDetailsPageTest.java # Product detail page tests
│       │   └── TestResultListener.java # Test result listener for reporting
│       └── resources/
│           ├── allure.properties  # Allure reporting configuration
│           ├── config.properties  # Test configuration
│           └── logback-test.xml   # Logging configuration
├── target/                        # Maven build artifacts and Allure results
├── test-results/                  # Generated test artifacts
│   ├── screenshots/               # Screenshots on failures
│   ├── videos/                    # Screen recordings (configurable via CLI)
│   └── traces/                    # Playwright traces for debugging
├── pom.xml                        # Optimized Maven configuration and dependencies
├── run-headless.sh               # Fast headless execution script
├── run-with-video.sh             # Headless with video recording script
├── run-visible.sh                # Visible mode debugging script
├── run-tests.sh                   # Original shell script for test execution
└── README.md                      # Comprehensive project documentation
```

## Architecture Overview

This structure follows industry best practices for test automation frameworks:

### **Page Object Model (POM)**
- **Separation of Concerns**: Test logic separated from page interactions
- **Maintainability**: Changes to UI only require updates to page objects
- **Reusability**: Page objects can be reused across multiple test classes
- **Readability**: Tests focus on business logic rather than implementation details

### **Framework Components**

#### **Configuration Layer** (`src/main/java/com/saucedemo/config/`)
- Centralized configuration management
- Environment-specific settings
- Test data configuration

#### **Page Object Layer** (`src/main/java/com/saucedemo/pages/`)
- **BasePage**: Common functionality shared across all pages
- **Specific Pages**: Individual page objects for each application page
- **Encapsulation**: Page-specific elements and actions

#### **Utility Layer** (`src/main/java/com/saucedemo/utils/`)
- **BrowserManager**: Optimized browser lifecycle and configuration management
  - Thread-safe Playwright initialization
  - Performance-tuned browser contexts
  - Smart cleanup with timeout protection
  - CLI-configurable video/trace capture
- **Helper Classes**: Common utilities and support functions

#### **Test Layer** (`src/test/java/com/saucedemo/tests/`)
- **BaseTest**: Common test setup and teardown functionality
- **Specific Test Classes**: Focused test suites for different application areas
- **Test Organization**: Tests grouped by functionality and business domain

#### **Resource Management** (`src/test/resources/`)
- **Configuration Files**: Environment and execution settings
- **Logging Configuration**: Structured logging setup
- **Allure Configuration**: Test reporting settings

### **Artifact Management**
- **allure-report/**: Generated test reports with detailed analytics
- **test-results/**: Smart evidence collection (screenshots, videos, traces)
  - Videos: CLI-configurable (OFF/ON_FAILURE/ON)
  - Traces: Saved on failure for deep debugging
  - Screenshots: Captured on failure by default
- **logs/**: Execution logs for debugging and monitoring
- **target/**: Maven build artifacts, compiled classes, and Allure results

### **Performance Optimizations**
- **Single JVM Execution**: Ensures proper Allure multi-class reporting
- **Enhanced JVM Settings**: G1GC, optimized heap (512MB-2GB)
- **Updated Dependencies**: Latest Playwright 1.48.0, JUnit 5.11.0, Allure 2.29.0
- **Browser Context Optimization**: Performance-tuned settings, smart resource blocking
- **Logging Optimization**: Reduced external library verbosity

### **Convenience Scripts**
- **./run-headless.sh**: Fast execution for CI/CD
- **./run-with-video.sh**: Headless with video recording on failure
- **./run-visible.sh**: Visible mode with debugging features

This structure promotes **scalability**, **maintainability**, **performance**, and **collaboration** in modern test automation development.
