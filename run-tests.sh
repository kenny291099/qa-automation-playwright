#!/bin/bash

# Sauce Demo Playwright Test Runner Script
# Usage: ./run-tests.sh [test-suite] [browser] [headless]

set -e

# Colors for pretty output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

# Default values
TEST_SUITE=${1:-"all"}
BROWSER=${2:-"chromium"}
HEADLESS=${3:-"true"}

echo -e "${BOLD}${BLUE}🚀 Playwright Test Automation Framework${NC}"
echo -e "${CYAN}================================================${NC}"
echo -e "${GREEN}📊 Test Suite:${NC} ${BOLD}$TEST_SUITE${NC}"
echo -e "${GREEN}🌐 Browser:${NC} ${BOLD}$BROWSER${NC}"
echo -e "${GREEN}👁️  Headless:${NC} ${BOLD}$HEADLESS${NC}"
echo -e "${CYAN}================================================${NC}"
echo ""

# Clean previous results and create necessary directories
echo -e "${YELLOW}🧹 Cleaning previous test results...${NC}"
rm -rf target/allure-results target/site/allure-maven-plugin >/dev/null 2>&1
mvn clean -q >/dev/null 2>&1

echo -e "${BLUE}📁 Creating test result directories...${NC}"
mkdir -p test-results/{screenshots,videos,traces} logs >/dev/null 2>&1

# Install Playwright browsers if not already installed
if [ ! -d ~/.cache/ms-playwright ]; then
    echo -e "${PURPLE}🔧 Installing Playwright browsers (this may take a moment)...${NC}"
    mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install --with-deps" -q >/dev/null 2>&1
fi

# Run tests based on suite selection
echo -e "${GREEN}🧪 Running tests...${NC}"
echo ""
case $TEST_SUITE in
    "login")
        echo -e "${CYAN}🔐 Running Login Tests...${NC}"
        mvn test -Dtest=LoginTest -Dbrowser=$BROWSER -Dheadless=$HEADLESS -B -q
        ;;
    "inventory")
        echo -e "${CYAN}📦 Running Inventory Tests...${NC}"
        mvn test -Dtest=InventoryTest -Dbrowser=$BROWSER -Dheadless=$HEADLESS -B -q
        ;;
    "e2e")
        echo -e "${CYAN}🚀 Running End-to-End Tests...${NC}"
        mvn test -Dtest=E2ETest -Dbrowser=$BROWSER -Dheadless=$HEADLESS -B -q
        ;;
    "all")
        echo -e "${CYAN}🎯 Running All Tests...${NC}"
        mvn test -Dbrowser=$BROWSER -Dheadless=$HEADLESS -B -q
        ;;
    *)
        echo -e "${RED}❌ Invalid test suite: $TEST_SUITE${NC}"
        echo -e "${YELLOW}Valid options: login, inventory, e2e, all${NC}"
        exit 1
        ;;
esac

# Generate Allure report
echo ""
TEST_EXIT_CODE=$?

if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✅ Tests completed successfully!${NC}"
    echo ""
    echo -e "${BLUE}📊 Generating Allure report...${NC}"
    mvn allure:report -B -q >/dev/null 2>&1
    
    echo -e "${GREEN}✅ Test execution completed successfully!${NC}"
    echo -e "${BLUE}📊 Allure report generated in: ${BOLD}target/site/allure-maven-plugin/${NC}"
    echo -e "${CYAN}🔗 To view the report interactively, run: ${BOLD}mvn allure:serve${NC}"
else
    echo -e "${RED}❌ Tests failed!${NC}"
    echo ""
    echo -e "${YELLOW}💡 Check the logs above for details or run with -X for debug info${NC}"
    exit $TEST_EXIT_CODE
fi
