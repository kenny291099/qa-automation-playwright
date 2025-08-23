#!/bin/bash

# Run tests with video recording enabled
echo "Running Playwright tests with video recording enabled..."

mvn clean test \
    -Dvideo.mode=ON \
    -Dtrace.mode=ON_FAILURE \
    -Dscreenshot.mode=ON_FAILURE \
    -Dheadless=true

echo "Tests completed. Check test-results/videos/ for recorded videos of failed tests."
echo "Check target/allure-results/ for Allure report data."
echo ""
echo "To generate Allure report:"
echo "mvn allure:serve"