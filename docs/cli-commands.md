# 🖥️ CLI Commands & Evidence Viewing

This document provides comprehensive command-line interface usage for running tests and viewing test evidence.

## Quick Start Commands

```bash
# Fast headless execution (recommended for CI/CD)
./run-headless.sh

# Headless with video recording (when tests fail)
./run-with-video.sh

# Visible mode with debugging features
./run-visible.sh
```

## Video Recording Commands

### Enable Video Recording
```bash
# Enable video recording for failed tests (recommended)
mvn test -Dvideo.mode=ON_FAILURE

# Enable video recording for all tests
mvn test -Dvideo.mode=ON

# Disable video recording (fastest execution)
mvn test -Dvideo.mode=OFF
```

### Video + Trace Combined
```bash
# Best for debugging failed tests
mvn test -Dvideo.mode=ON_FAILURE -Dtrace.mode=ON_FAILURE

# Full debugging mode (all evidence)
mvn test -Dvideo.mode=ON -Dtrace.mode=ON -Dscreenshot.mode=ALWAYS
```

## Playwright Trace Commands

### Generate Traces
```bash
# Enable traces on test failure (recommended)
mvn test -Dtrace.mode=ON_FAILURE

# Enable traces for all tests (debugging)
mvn test -Dtrace.mode=ON

# Disable traces (fastest execution)
mvn test -Dtrace.mode=OFF
```

### View Traces
```bash
# View a specific trace file
npx playwright show-trace test-results/traces/LoginTest_failure_20241122_154530.zip

# View most recent trace
npx playwright show-trace test-results/traces/$(ls -t test-results/traces/*.zip | head -1)

# Open traces directory
open test-results/traces/
```

### Trace Analysis Features
When viewing traces, you can:
- **Timeline**: See exact timing of each action
- **Screenshots**: Visual progression of the test
- **Network**: Monitor API calls and responses
- **Console**: View browser console logs
- **Sources**: Inspect page source at any point

## Evidence File Management

### View Evidence Directories
```bash
# Open all test results
open test-results/

# View screenshots
open test-results/screenshots/

# View videos
open test-results/videos/

# View traces
open test-results/traces/
```

### Find Specific Evidence
```bash
# Find videos for a specific test
find test-results/videos -name "*LoginTest*" -type f

# Find traces for failed tests
find test-results/traces -name "*failure*" -type f -exec ls -la {} \;

# Find recent screenshots
find test-results/screenshots -name "*.png" -mtime -1
```

## Browser-Specific Commands

### Chrome/Chromium
```bash
# Run with Chrome and video recording
mvn test -Dbrowser=chromium -Dvideo.mode=ON_FAILURE

# Debug mode with Chrome
mvn test -Dbrowser=chromium -Dheadless=false -Dslow.mo=500 -Dtrace.mode=ON
```

### Firefox
```bash
# Run with Firefox and evidence capture
mvn test -Dbrowser=firefox -Dvideo.mode=ON_FAILURE -Dtrace.mode=ON_FAILURE

# Firefox visible mode
mvn test -Dbrowser=firefox -Dheadless=false -Dvideo.mode=ON
```

### WebKit/Safari
```bash
# Run with WebKit
mvn test -Dbrowser=webkit -Dvideo.mode=ON_FAILURE

# WebKit debugging
mvn test -Dbrowser=webkit -Dheadless=false -Dtrace.mode=ON -Dslow.mo=1000
```

## Test Execution Patterns

### Single Test Debugging
```bash
# Debug a specific failing test with full evidence
mvn test -Dtest=LoginTest#testSuccessfulLogin \
  -Dheadless=false \
  -Dvideo.mode=ON \
  -Dtrace.mode=ON \
  -Dslow.mo=1000
```

### CI/CD Optimized
```bash
# Fast CI/CD execution with failure evidence
mvn clean test \
  -Dheadless=true \
  -Dvideo.mode=ON_FAILURE \
  -Dtrace.mode=ON_FAILURE \
  -Dscreenshot.mode=ON_FAILURE
```

### Performance Testing
```bash
# Fast execution without evidence collection
mvn test \
  -Dvideo.mode=OFF \
  -Dtrace.mode=OFF \
  -Dscreenshot.mode=OFF \
  -Dheadless=true
```

## Evidence Analysis Workflow

### 1. After Test Failure
```bash
# Check what evidence was captured
ls -la test-results/*/

# View the latest failure video
open test-results/videos/$(ls -t test-results/videos/*.webm | head -1)

# Analyze trace for detailed debugging
npx playwright show-trace test-results/traces/$(ls -t test-results/traces/*failure*.zip | head -1)
```

### 2. Trace Analysis Steps
1. **Open trace**: `npx playwright show-trace path/to/trace.zip`
2. **Review timeline**: Check action timing and duration
3. **Inspect screenshots**: Visual state at each step
4. **Check network**: API calls and responses
5. **Review console**: JavaScript errors or warnings
6. **Examine DOM**: Element states and changes

### 3. Video Analysis
- **Playback speed**: Adjust to see actions clearly
- **Frame-by-frame**: Use arrow keys for precise navigation
- **Compare states**: Before/after critical actions
- **Identify timing issues**: Actions happening too fast/slow

## Integration with Allure Reports

### Generate Reports with Evidence
```bash
# Run tests with evidence capture
mvn clean test -Dvideo.mode=ON_FAILURE -Dtrace.mode=ON_FAILURE

# Generate Allure report
mvn allure:serve

# Or generate static report
mvn allure:report
open target/site/allure-maven-plugin/index.html
```

### Evidence in Allure
- **Screenshots**: Automatically attached to failed tests
- **Videos**: Linked when available
- **Traces**: Can be downloaded from report
- **Test steps**: Detailed breakdown with evidence

## Troubleshooting Commands

### Clean Evidence
```bash
# Clean all evidence
rm -rf test-results/

# Clean specific evidence type
rm -rf test-results/videos/
rm -rf test-results/traces/
rm -rf test-results/screenshots/
```

### Verify Setup
```bash
# Check Playwright installation
npx playwright --version

# Test trace viewer
npx playwright show-trace --help

# Check available browsers
npx playwright install --help
```

### Storage Management
```bash
# Check evidence storage usage
du -sh test-results/

# Clean old evidence (older than 7 days)
find test-results/ -type f -mtime +7 -delete

# Keep only recent traces
find test-results/traces/ -name "*.zip" -mtime +3 -delete
```

## Best Practices

### Development
- Use `./run-visible.sh` for test development
- Enable traces for complex debugging: `-Dtrace.mode=ON`
- Use slow motion for step observation: `-Dslow.mo=1000`

### CI/CD
- Use `./run-with-video.sh` for failure evidence
- Configure retention policies for evidence files
- Archive important traces for later analysis

### Performance
- Use `./run-headless.sh` for fastest execution
- Disable evidence for load testing
- Monitor storage usage in long-running environments

This comprehensive CLI approach enables efficient test development, debugging, and maintenance across different environments and scenarios.