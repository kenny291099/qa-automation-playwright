#!/bin/bash

# Run tests in headless mode without video (faster execution)
echo "Running Playwright tests in optimized headless mode..."

mvn clean test \
    -Dvideo.mode=OFF \
    -Dtrace.mode=ON_FAILURE \
    -Dscreenshot.mode=ON_FAILURE \
    -Dheadless=true

echo "Tests completed. Check test-results/screenshots/ for failure screenshots."
echo "Check test-results/traces/ for failure traces."
echo ""
echo "To generate Allure report:"
echo "mvn allure:serve"