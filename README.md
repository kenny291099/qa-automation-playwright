# QA Automation Engineer Portfolio  
### Playwright + Java Test Automation Framework  

Hello! I'm Kenny, a QA Automation Engineer passionate about building robust testing solutions that help teams deliver high-quality software with confidence.  

This repository showcases my learning journey and practical skills in building **scalable, maintainable test automation frameworks** using **Playwright with Java**.  

---

## 🚀 Key Features
- **Playwright + Java 17** – Modern, cross-browser automation with latest optimizations
- **Page Object Model (POM)** – Clean, maintainable test structure  
- **Allure Reports** – Rich, visual reporting & analytics with multi-class aggregation
- **CI/CD Ready** – Seamless integration with GitHub Actions, Jenkins, GitLab  
- **Cross-Browser Support** – Chromium, Firefox, WebKit with performance optimizations
- **Smart Evidence Capture** – Configurable screenshots, videos, and traces
- **Performance Optimized** – Enhanced JVM settings, G1GC, optimized dependencies
- **Single JVM Execution** – Ensures proper Allure multi-class reporting  

---

## 🛠️ Technologies Used

| Technology        | Version | Key Benefits |
|-------------------|---------|--------------|
| **Playwright**    | 1.48.0  | Superior browser automation, excellent debugging |
| **Java 17**       | 17+     | Modern language features, enterprise reliability |
| **Maven**         | 3.13+   | Optimized dependency management and build automation |
| **JUnit 5**       | 5.11.0  | Modern testing framework with flexible organization |
| **Allure**        | 2.29.0  | Professional reporting with visual insights |
| **GitHub Actions**| Latest  | Streamlined CI/CD integration |

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

## 🎯 CLI Commands & Test Execution

### Quick Start Commands
```bash
# Fast headless execution (recommended for CI/CD)
./run-headless.sh

# Headless with video recording (when tests fail)
./run-with-video.sh

# Visible mode with debugging features
./run-visible.sh
```

### Video Recording & Evidence Capture
```bash
# Enable video recording for all tests
mvn test -Dvideo.mode=ON

# Enable video recording with traces on failure
mvn test -Dvideo.mode=ON -Dtrace.mode=ON_FAILURE

# Screenshots only on failure (default, fastest)
mvn test -Dvideo.mode=OFF -Dtrace.mode=OFF -Dscreenshot.mode=ON_FAILURE

# Record everything (debugging mode)
mvn test -Dvideo.mode=ON -Dtrace.mode=ON -Dscreenshot.mode=ALWAYS
```

### Viewing Test Evidence
```bash
# View recorded videos (saved automatically on failures when enabled)
open test-results/videos/

# View Playwright traces (for deep debugging)
npx playwright show-trace test-results/traces/LoginTest_failure_20241122_154530.zip

# View screenshots
open test-results/screenshots/

# Generate and view Allure report
mvn allure:serve
```

### Running Specific Tests
```bash
# Run a specific test class
mvn test -Dtest=LoginTest
mvn test -Dtest=CartPageTest -Dvideo.mode=ON

# Run a specific test method
mvn test -Dtest=LoginTest#testSuccessfulLogin
mvn test -Dtest=InventoryTest#testAddToCart -Dtrace.mode=ON_FAILURE

# Run tests matching a pattern
mvn test -Dtest=*Login* -Dvideo.mode=ON
mvn test -Dtest=*Cart* -Dheadless=false
```

### Browser Configuration
```bash
# Run with different browsers
mvn test -Dbrowser=chromium -Dvideo.mode=ON
mvn test -Dbrowser=firefox -Dtrace.mode=ON_FAILURE
mvn test -Dbrowser=webkit -Dheadless=false

# Headless vs visible mode
mvn test -Dheadless=true -Dvideo.mode=ON    # Fast CI/CD execution
mvn test -Dheadless=false -Dslow.mo=500     # Debugging with slow motion
```

### Performance & Debugging
```bash
# Debug failing tests with full evidence
mvn test -Dtest=LoginTest -Dheadless=false -Dvideo.mode=ON -Dtrace.mode=ON -Dslow.mo=1000

# Fast execution without evidence capture
mvn test -Dvideo.mode=OFF -Dtrace.mode=OFF -Dscreenshot.mode=OFF

# Memory optimization for large test suites
mvn test -Xms1g -Xmx3g -Dvideo.mode=OFF
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
- [CLI Commands & Evidence Viewing](./docs/cli-commands.md) ⭐  
- [Configuration](./docs/configuration.md)  
- [Project Structure](./docs/project-structure.md)  
- [Test Coverage](./docs/test-coverage.md)  
- [CI/CD Integration](./docs/ci-cd.md)  
- [Troubleshooting](./docs/troubleshooting.md)  

---

## 🌐 Connect with Me
- [LinkedIn](https://www.linkedin.com/in/kenny-wirianto/)  
- [GitHub](https://github.com/kenny291099)  
- 📧 kenny291099@gmail.com  
