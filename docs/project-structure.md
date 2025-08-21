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
├── target/                        # Maven build artifacts
├── test-results/                  # Generated test artifacts
│   ├── screenshots/               # Screenshots on failures
│   ├── videos/                    # Screen recordings (if enabled)
│   └── traces/                    # Playwright traces for debugging
├── pom.xml                        # Maven configuration and dependencies
├── run-tests.sh                   # Shell script for test execution
└── README.md                      # Main project documentation
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
- **BrowserManager**: Browser lifecycle and configuration management
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
- **test-results/**: Evidence collection (screenshots, videos, traces)
- **logs/**: Execution logs for debugging and monitoring
- **target/**: Maven build artifacts and compiled classes

This structure promotes **scalability**, **maintainability**, and **collaboration** in test automation development.
