#!/bin/bash

# Run tests in visible mode (for debugging)
echo "Running Playwright tests in visible mode..."

mvn clean test \
    -Dvideo.mode=ON \
    -Dtrace.mode=ON_FAILURE \
    -Dscreenshot.mode=ON_FAILURE \
    -Dheadless=false \
    -Dslow.mo=500

echo "Tests completed. Check test-results/ folder for recordings and traces."
echo ""
echo "To generate Allure report:"
echo "mvn allure:serve"