# ⚙️ Configuration

This document explains how to configure the optimized Playwright test automation framework for different environments and execution scenarios.

## Recent Optimizations

- **Performance**: Updated to Playwright 1.48.0, JUnit 5.11.0, Allure 2.29.0
- **JVM Optimization**: Enhanced memory settings (512MB-2GB heap), G1GC
- **Single JVM Execution**: Ensures proper Allure multi-class reporting
- **Smart Evidence**: CLI-configurable video/trace capture
- **Browser Optimization**: Performance-tuned context settings

## Configuration Files

### Main Configuration (`src/test/resources/config.properties`)

```properties
# Application settings
base.url=https://www.saucedemo.com  # Target application URL
browser=chromium                    # Default browser: chromium, firefox, webkit
headless=true                      # Run in headless mode: true/false
timeout=30000                      # Default timeout in milliseconds
slow.mo=0                          # Slow motion in milliseconds for debugging

# Evidence collection settings
video.mode=ON_FAILURE              # Video recording: OFF, ON_FAILURE, ALWAYS
screenshot.mode=ON_FAILURE         # Screenshot capture: OFF, ON_FAILURE, ALWAYS
trace.mode=ON_FAILURE              # Playwright traces: OFF, ON_FAILURE, ALWAYS

# Test execution settings
parallel.workers=1                 # Number of parallel test workers
retry.count=1                      # Number of retries for failed tests
```

### Allure Configuration (`src/test/resources/allure.properties`)

```properties
allure.results.directory=target/allure-results
allure.link.issue.pattern=https://github.com/your-org/your-repo/issues/{}
allure.link.tms.pattern=https://your-tms.com/test/{}
```

### Logging Configuration (`src/test/resources/logback-test.xml`)

Controls logging levels and output formats for test execution monitoring.

## Configuration Options

### Browser Configuration

| Property | Values | Description |
|----------|--------|-------------|
| `browser` | `chromium`, `firefox`, `webkit` | Browser engine for test execution |
| `headless` | `true`, `false` | Run browser in headless mode |
| `slow.mo` | `0-5000` (ms) | Delay between actions for debugging |

### Environment Configuration

| Property | Values | Description |
|----------|--------|-------------|
| `base.url` | URL string | Target application base URL |
| `timeout` | Milliseconds | Default timeout for operations |
| `environment` | `dev`, `staging`, `prod` | Environment identifier |

### Evidence Collection

| Property | Values | Description |
|----------|--------|-------------|
| `screenshot.mode` | `OFF`, `ON_FAILURE`, `ALWAYS` | When to capture screenshots |
| `video.mode` | `OFF`, `ON_FAILURE`, `ALWAYS` | When to record videos |
| `trace.mode` | `OFF`, `ON_FAILURE`, `ALWAYS` | When to collect Playwright traces |

### Execution Control

| Property | Values | Description |
|----------|--------|-------------|
| `parallel.workers` | `1-8` | Number of parallel test workers |
| `retry.count` | `0-3` | Number of retries for failed tests |
| `test.groups` | Group names | Specific test groups to execute |

## System Property Overrides

You can override any configuration property using Maven system properties:

### Basic Overrides
```bash
# Change browser
mvn test -Dbrowser=firefox

# Run in headed mode
mvn test -Dheadless=false

# Increase timeout
mvn test -Dtimeout=60000

# Enable slow motion for debugging
mvn test -Dslow.mo=1000
```

### Evidence Collection Overrides
```bash
# Enable video recording on failure (recommended)
mvn test -Dvideo.mode=ON_FAILURE

# Enable video recording for all tests
mvn test -Dvideo.mode=ON

# Enable traces on failure for deep debugging
mvn test -Dtrace.mode=ON_FAILURE

# Debug failing tests with full evidence
mvn test -Dvideo.mode=ON -Dtrace.mode=ON_FAILURE -Dscreenshot.mode=ON_FAILURE

# View Playwright traces after test
npx playwright show-trace test-results/traces/TestName_failure_timestamp.zip
```

### Environment-Specific Overrides
```bash
# Test against different environment
mvn test -Dbase.url=https://staging.saucedemo.com

# Use environment-specific configuration
mvn test -Denvironment=staging

# Run specific test suite
mvn test -Dtest=LoginTest
```

### Performance and Parallel Execution
```bash
# Increase parallel workers
mvn test -Dparallel.workers=4

# Control retry behavior
mvn test -Dretry.count=2

# Optimize for CI environment
mvn test -Dparallel.workers=2 -Dheadless=true -Dtimeout=45000
```

## Environment-Specific Configurations

### Development Environment
```bash
mvn test \
  -Dbase.url=http://localhost:3000 \
  -Dheadless=false \
  -Dslow.mo=500 \
  -Dscreenshot.mode=ALWAYS
```

### Staging Environment
```bash
mvn test \
  -Dbase.url=https://staging.saucedemo.com \
  -Dheadless=true \
  -Dparallel.workers=2 \
  -Dtimeout=45000
```

### Production Smoke Tests
```bash
mvn test \
  -Dbase.url=https://www.saucedemo.com \
  -Dheadless=true \
  -Dtest=*SmokeTest* \
  -Dtimeout=30000
```

### CI/CD Environment
```bash
# Optimized for CI/CD with evidence collection
mvn test \
  -Dheadless=true \
  -Dvideo.mode=ON_FAILURE \
  -Dtrace.mode=ON_FAILURE \
  -Dscreenshot.mode=ON_FAILURE

# Use convenience scripts
./run-headless.sh        # Fast execution without videos
./run-with-video.sh      # Headless with video on failure
```

## Advanced Configuration

### Custom Configuration Files

You can create environment-specific configuration files:

#### `config-staging.properties`
```properties
base.url=https://staging.saucedemo.com
timeout=45000
parallel.workers=2
video.mode=ALWAYS
```

#### `config-production.properties`
```properties
base.url=https://www.saucedemo.com
timeout=30000
parallel.workers=1
screenshot.mode=ON_FAILURE
```

### Loading Custom Configuration
```bash
# Use custom configuration file
mvn test -Dconfig.file=config-staging.properties

# Combine with system property overrides
mvn test -Dconfig.file=config-staging.properties -Dheadless=false
```

### Browser-Specific Settings

```bash
# Firefox with specific settings
mvn test -Dbrowser=firefox -Dtimeout=45000

# WebKit with debugging
mvn test -Dbrowser=webkit -Dheadless=false -Dslow.mo=1000

# Chromium optimized for CI
mvn test -Dbrowser=chromium -Dheadless=true -Dparallel.workers=4
```

## Configuration Best Practices

### Development
- Use `headless=false` for debugging
- Enable `slow.mo` for step-by-step observation
- Set `screenshot.mode=ALWAYS` for visual feedback
- Use single worker (`parallel.workers=1`) for debugging

### CI/CD
- Always use `headless=true`
- Optimize `parallel.workers` based on available resources
- Use `ON_FAILURE` modes for evidence collection
- Set appropriate timeouts for network conditions

### Performance Testing
- Increase `parallel.workers` for load simulation
- Adjust `timeout` values for performance scenarios
- Disable unnecessary evidence collection
- Use specific browser configurations

### Cross-Browser Testing
- Create browser-specific configuration profiles
- Use matrix strategies in CI/CD
- Test with different browser-specific timeouts
- Validate browser-specific behaviors

## Configuration Validation

The framework automatically validates configuration values and provides helpful error messages for invalid settings. Common validation includes:

- Browser name validation
- Timeout range checking
- URL format validation
- Worker count limits
- Evidence mode validation

## Environment Variables

You can also use environment variables that map to system properties:

```bash
export BROWSER=firefox
export HEADLESS=false
export BASE_URL=https://staging.saucedemo.com
mvn test
```

This flexible configuration system allows you to adapt the framework to any environment or execution scenario while maintaining consistency and reliability across different setups.
