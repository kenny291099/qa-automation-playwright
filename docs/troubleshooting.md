# 🐞 Troubleshooting

## Common Issues & Solutions

### 1. Playwright browsers not installed
**Problem**: Tests fail with browser-related errors
**Solution**:
```bash
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
```

### 2. Tests failing in headless mode
**Problem**: Tests pass locally but fail in headless CI environments
**Solution**:
```bash
# Run in headed mode for debugging
mvn test -Dheadless=false

# Or run with specific browser for testing
mvn test -Dbrowser=chromium -Dheadless=false
```

### 3. Slow test execution
**Problem**: Tests running too slowly
**Solutions**:
```bash
# Add slow motion for debugging (increases delay between actions)
mvn test -Dslow.mo=1000

# Reduce parallel workers if resource-constrained
mvn test -Dparallel.workers=1

# Run specific test class instead of full suite
mvn test -Dtest=LoginTest
```

### 4. Allure port conflicts
**Problem**: Allure report server won't start
**Solution**:
```bash
# Specify custom port
mvn allure:serve -Dallure.serve.port=8080

# Or try a different port
mvn allure:serve -Dallure.serve.port=9999
```

### 5. Maven compilation issues
**Problem**: Compilation failures or dependency issues
**Solutions**:
```bash
# Clean and reinstall dependencies
mvn clean compile

# Force update snapshots and dependencies
mvn clean compile -U

# Clear local Maven cache if needed
rm -rf ~/.m2/repository/com/microsoft/playwright
mvn clean compile
```

### 6. Test data or environment issues
**Problem**: Tests fail due to test data or environment configuration
**Solutions**:
```bash
# Run with different environment settings
mvn test -Dbase.url=https://staging.saucedemo.com

# Reset test environment between runs
mvn test -Dreset.state=true

# Use specific test user credentials
mvn test -Dtest.user=standard_user -Dtest.password=secret_sauce
```

### 7. Screenshot/Video capture issues
**Problem**: Screenshots or videos not being captured
**Solutions**:
```bash
# Force screenshot capture
mvn test -Dscreenshot.mode=ALWAYS

# Enable video recording
mvn test -Dvideo.mode=ALWAYS

# Enable all evidence collection
mvn test -Dtrace.mode=ALWAYS -Dvideo.mode=ALWAYS -Dscreenshot.mode=ALWAYS
```

## Debug Modes

### Advanced Debug Mode
Enable comprehensive debugging with full evidence collection:
```bash
mvn test -Dtrace.mode=ALWAYS -Dvideo.mode=ALWAYS -Dheadless=false -Dslow.mo=500
```

### Specific Browser Debug
Debug with specific browser:
```bash
# Debug with Firefox
mvn test -Dbrowser=firefox -Dheadless=false -Dtrace.mode=ALWAYS

# Debug with WebKit
mvn test -Dbrowser=webkit -Dheadless=false -Dvideo.mode=ALWAYS
```

### Test-Specific Debug
Debug specific failing tests:
```bash
# Debug specific test class
mvn test -Dtest=CartPageTest -Dheadless=false -Dtrace.mode=ALWAYS

# Debug specific test method
mvn test -Dtest=CartPageTest#testAddProductToCart -Dheadless=false
```

## Logging & Analysis

### Enable Detailed Logging
```bash
# Run with debug logging
mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG

# Check test execution logs
tail -f logs/test.log
```

### Analyze Test Results
```bash
# Generate detailed Allure report
mvn allure:report

# Serve report for analysis
mvn allure:serve

# Check test artifacts
ls -la test-results/
```

## Performance Issues

### Memory Issues
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"
mvn test

# Run with single worker to reduce memory usage
mvn test -Dparallel.workers=1
```

### Timeout Issues
```bash
# Increase default timeout
mvn test -Dtimeout=60000

# Run with longer timeouts for slower environments
mvn test -Dtimeout=90000 -Dslow.mo=1000
```

## CI/CD Specific Issues

### GitHub Actions Issues
- Ensure browsers are installed in CI workflow
- Use appropriate matrix strategy for cross-browser testing
- Check artifact upload/download configuration

### Docker/Container Issues
```bash
# Run tests in container with proper display setup
mvn test -Dheadless=true -Dbrowser=chromium

# Ensure proper user permissions in containers
mvn test -Duser.home=/tmp
```

## Getting Help

If issues persist:
1. Check the `logs/test.log` file for detailed error information
2. Review generated screenshots in `test-results/screenshots/`
3. Analyze Playwright traces in `test-results/traces/`
4. Examine video recordings in `test-results/videos/`
5. Consult the [Playwright Java documentation](https://playwright.dev/java/)
6. Review framework-specific configuration in `src/test/resources/config.properties`
