# QA Automation Engineer Portfolio  
### Playwright + Java Test Automation Framework  

Hello! I'm Kenny, a QA Automation Engineer passionate about building robust testing solutions that help teams deliver high-quality software with confidence.  

This repository showcases my learning journey and practical skills in building **scalable, maintainable test automation frameworks** using **Playwright with Java**.  

---

## 🚀 Key Features
- **Playwright + Java 17** – Modern, cross-browser automation  
- **Page Object Model (POM)** – Clean, maintainable test structure  
- **Allure Reports** – Rich, visual reporting & analytics  
- **CI/CD Ready** – Seamless integration with GitHub Actions, Jenkins, GitLab  
- **Cross-Browser Support** – Chromium, Firefox, WebKit  
- **Evidence Capture** – Screenshots, videos, and traces on failures  

---

## 🛠️ Technologies Used

| Technology        | Key Benefits |
|-------------------|--------------|
| **Playwright**    | Superior browser automation, excellent debugging |
| **Java 17**       | Modern language features, enterprise reliability |
| **Maven**         | Dependency management and build automation |
| **JUnit 5**       | Modern testing framework with flexible organization |
| **Allure**        | Professional reporting with visual insights |
| **GitHub Actions**| Streamlined CI/CD integration |

---

## ⚡ Quick Start

```bash
git clone https://github.com/kenny291099/qa-automation-playwright.git
cd qa-automation-playwright

# Install dependencies
mvn clean compile

# Install Playwright browsers
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"

# Run tests
mvn test
```

Generate reports:
```bash
mvn allure:serve
```

## 🎯 Running Specific Tests

### Run a specific test class:
```bash
mvn test -Dtest=LoginTest
mvn test -Dtest=CartPageTest
mvn test -Dtest=CheckoutPageTest
```

### Run a specific test method:
```bash
mvn test -Dtest=LoginTest#testSuccessfulLogin
mvn test -Dtest=InventoryTest#testAddToCart

# For visible browser testing, always run single methods to avoid multiple browser instances:
mvn test -Dbrowser=chrome -Dheadless=false -Dtest=LoginTest#testSuccessfulLogin
```

### Run tests matching a pattern:
```bash
# Run all tests with "Login" in the class name
mvn test -Dtest=*Login*

# Run all tests with "Cart" in the class name  
mvn test -Dtest=*Cart*

# Run multiple test classes
mvn test -Dtest=LoginTest,CartPageTest
```

### Run tests by browser:
```bash
mvn test -Dbrowser=chrome      # or -Dbrowser=chromium
mvn test -Dbrowser=firefox
mvn test -Dbrowser=webkit      # or -Dbrowser=safari (same engine)
mvn test -Dbrowser=safari      # Uses Safari on macOS, WebKit elsewhere
```

### Run tests in headless mode:
```bash
mvn test -Dheadless=true
```

## 🌐 **Browser-Specific Notes**

### **Chrome/Chromium:**
- ✅ **Best compatibility** with modern web features
- ✅ **Fastest cleanup** in visible mode
- ✅ **Uses Google Chrome** when available on macOS for visible mode

### **Firefox:**
- ✅ **Good compatibility** with web standards
- ✅ **Reliable performance** across platforms
- ✅ **Clean resource management**

### **WebKit/Safari:**
- ✅ **Uses Playwright WebKit engine** (Safari-compatible) 
- ⚠️ **Slightly slower cleanup** (4s timeout vs 3s for Chrome)
- ✅ **Minimal launch arguments** for better stability
- 💡 **Note:** `webkit` and `safari` are aliases for the same Playwright WebKit engine

## 💡 **Visible vs Headless Mode Best Practices**

### **Visible Mode (Debugging & Development):**
```bash
# ✅ WORKS: Single test methods (fastest for debugging specific issues)
mvn test -Dbrowser=chrome -Dheadless=false -Dtest=LoginTest#testSuccessfulLogin
mvn test -Dbrowser=safari -Dheadless=false -Dtest=LoginTest#testSuccessfulLogin

# ✅ WORKS: Full test classes (enhanced cleanup prevents browser accumulation)
mvn test -Dbrowser=chrome -Dheadless=false -Dtest=LoginTest
mvn test -Dbrowser=safari -Dheadless=false -Dtest=LoginTest

# ✅ WORKS: Multiple test classes
mvn test -Dbrowser=chrome -Dheadless=false -Dtest=LoginTest,CartPageTest
```

### **Headless Mode (CI/CD & Full Test Runs):**
```bash
# ✅ RECOMMENDED: Full test classes and suites (faster execution)
mvn test -Dheadless=true -Dtest=LoginTest
mvn test -Dheadless=true  # Run all tests
```

---

## 📖 Documentation
- [Test Coverage](./docs/test-coverage.md)  
- [Project Structure](./docs/project-structure.md)  
- [CI/CD Integration](./docs/ci-cd.md)  
- [Configuration](./docs/configuration.md)  
- [Troubleshooting](./docs/troubleshooting.md)  
- [Best Practices](./docs/framework-best-practices.md)  

---

## 🌐 Connect with Me
- [LinkedIn](https://www.linkedin.com/in/kenny-wirianto/)  
- [GitHub](https://github.com/kenny291099)  
- 📧 kenny291099@gmail.com  
