# Sauce Demo Playwright Automation

A comprehensive test automation framework for the [Sauce Demo](https://www.saucedemo.com/) e-commerce website using **Playwright** with **Java 17**, featuring **Page Object Model (POM)** design pattern, **Allure reporting**, and **CI/CD integration**.

## 🚀 Features

- ✅ **Playwright** with Java 17 for cross-browser testing
- ✅ **Page Object Model (POM)** for maintainable test structure
- ✅ **Allure reporting** with rich test reports and attachments
- ✅ **CI/CD integration** with GitHub Actions
- ✅ **Cross-browser testing** (Chromium, Firefox, WebKit)
- ✅ **Screenshot and video recording** on test failures
- ✅ **Trace recording** for debugging
- ✅ **Parallel test execution** support
- ✅ **Comprehensive logging** with Logback
- ✅ **Configuration management** with Owner library

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Git**

## 🛠️ Project Structure

```
qa-automation-playwright/
├── .github/
│   └── workflows/
│       └── ci.yml                 # GitHub Actions CI/CD pipeline
├── src/
│   ├── main/java/com/saucedemo/
│   │   ├── config/
│   │   │   └── TestConfig.java    # Configuration interface
│   │   ├── pages/                 # Page Object Model classes
│   │   │   ├── BasePage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── InventoryPage.java
│   │   │   ├── ProductDetailsPage.java
│   │   │   ├── CartPage.java
│   │   │   └── CheckoutPage.java
│   │   └── utils/
│   │       └── BrowserManager.java # Browser lifecycle management
│   └── test/
│       ├── java/com/saucedemo/tests/
│       │   ├── BaseTest.java      # Base test class
│       │   ├── TestResultListener.java # Test result listener
│       │   ├── LoginTest.java     # Login functionality tests
│       │   ├── InventoryTest.java # Inventory management tests
│       │   └── E2ETest.java       # End-to-end workflow tests
│       └── resources/
│           ├── config.properties  # Test configuration
│           ├── allure.properties  # Allure configuration
│           └── logback-test.xml   # Logging configuration
├── pom.xml                        # Maven configuration
└── README.md
```

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd qa-automation-playwright
```

### 2. Install Dependencies

```bash
mvn clean compile
```

### 3. Install Playwright Browsers

```bash
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
```

### 4. Run Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=LoginTest

# Run with specific browser
mvn test -Dbrowser=firefox

# Run in headed mode (non-headless)
mvn test -Dheadless=false

# Run with custom configuration
mvn test -Dbrowser=webkit -Dheadless=false -Dslow.mo=1000
```

## 📊 Reporting

### Generate Allure Report

```bash
# Generate Allure report
mvn allure:report

# Serve Allure report (opens in browser)
mvn allure:serve
```

The Allure report will be available at `http://localhost:port` and includes:
- Test execution results
- Screenshots on failures
- Test traces for debugging
- Detailed step-by-step execution
- Environment information
- Historical trends

## ⚙️ Configuration

### Test Configuration (`src/test/resources/config.properties`)

```properties
# Browser settings
base.url=https://www.saucedemo.com
browser=chromium                    # chromium, firefox, webkit
headless=true                      # true/false
slow.mo=0                          # Slow motion in milliseconds

# Timeouts
timeout=30000                      # Default timeout in milliseconds

# Recording settings
video.mode=ON_FAILURE              # OFF, ON_FAILURE, ALWAYS
screenshot.mode=ON_FAILURE         # OFF, ON_FAILURE, ALWAYS
trace.mode=ON_FAILURE              # OFF, ON_FAILURE, ALWAYS

# Execution settings
parallel.workers=1                 # Number of parallel workers
retry.count=1                      # Number of retries for failed tests
```

### System Properties Override

You can override configuration using system properties:

```bash
mvn test -Dbrowser=firefox -Dheadless=false -Dtimeout=60000
```

## 🧪 Test Scenarios

### Login Tests (`LoginTest.java`)
- ✅ Successful login with valid credentials
- ✅ Login failure with invalid password
- ✅ Login failure with locked out user
- ✅ Login validation with empty fields
- ✅ Error message handling
- ✅ Form accessibility checks

### Inventory Tests (`InventoryTest.java`)
- ✅ Product display verification
- ✅ Add/remove products to/from cart
- ✅ Product sorting functionality
- ✅ Navigation to product details
- ✅ Shopping cart management
- ✅ Menu functionality
- ✅ Logout functionality

### End-to-End Tests (`E2ETest.java`)
- ✅ Complete purchase flow (single item)
- ✅ Complete purchase flow (multiple items)
- ✅ Shopping cart management throughout flow
- ✅ Checkout validation
- ✅ Price consistency verification
- ✅ Cancel checkout process

## 🔄 CI/CD Pipeline

The project includes a comprehensive GitHub Actions pipeline that:

- **Triggers**: Push to main/develop, Pull Requests, Scheduled runs, Manual dispatch
- **Matrix Testing**: Runs tests across Chromium, Firefox, and WebKit
- **Parallel Execution**: Tests run in parallel across different browsers
- **Artifact Collection**: Screenshots, videos, traces, and logs
- **Report Generation**: Automated Allure report generation
- **GitHub Pages**: Deploys reports to GitHub Pages
- **Notifications**: Test result summaries

### Manual Workflow Dispatch

You can trigger tests manually with custom parameters:
- Browser selection (chromium, firefox, webkit)
- Headless/headed mode
- Specific test suite (all, login, inventory, e2e)

## 📁 Test Artifacts

Test execution generates the following artifacts:

```
test-results/
├── screenshots/          # Screenshots on failures
├── videos/              # Screen recordings (if enabled)
└── traces/              # Playwright traces for debugging

logs/
└── test.log             # Application logs

target/
├── allure-results/      # Raw Allure test results
└── site/allure-maven-plugin/  # Generated Allure reports
```

## 🛠️ Development

### Adding New Tests

1. Create new test class extending `BaseTest`
2. Use Page Objects from `com.saucedemo.pages` package
3. Add Allure annotations for better reporting
4. Follow the existing naming conventions

### Adding New Page Objects

1. Extend `BasePage` class
2. Define locators as private final fields
3. Implement page-specific methods with `@Step` annotations
4. Return appropriate page objects for method chaining

### Best Practices

- **Use Page Object Model**: Keep test logic separate from page interactions
- **Allure Annotations**: Use `@Step`, `@Epic`, `@Feature`, `@Story` for better reporting
- **Assertions**: Use descriptive assertion messages
- **Logging**: Add appropriate logging for debugging
- **Wait Strategies**: Use explicit waits instead of Thread.sleep()

## 🐛 Troubleshooting

### Common Issues

1. **Playwright browsers not installed**
   ```bash
   mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
   ```

2. **Tests failing in headless mode**
   ```bash
   mvn test -Dheadless=false
   ```

3. **Slow test execution**
   ```bash
   mvn test -Dslow.mo=1000  # Add 1 second delay between actions
   ```

4. **Port conflicts**
   ```bash
   mvn allure:serve -Dallure.serve.port=8080
   ```

### Debug Mode

Enable debug mode for detailed execution traces:

```bash
mvn test -Dtrace.mode=ALWAYS -Dvideo.mode=ALWAYS -Dheadless=false
```

## 📚 Resources

- [Playwright Java Documentation](https://playwright.dev/java/)
- [Allure Framework](https://docs.qameta.io/allure/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [GitHub Actions](https://docs.github.com/en/actions)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add/update tests as needed
5. Run the test suite
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Happy Testing! 🚀**
