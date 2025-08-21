# ⚙️ CI/CD Integration

This framework is designed to integrate seamlessly with modern CI/CD platforms.

## GitHub Actions
- Run tests on PRs and merges
- Cross-browser matrix (Chromium, Firefox, WebKit)
- Collect artifacts (logs, screenshots, videos)
- Generate and publish Allure reports

Sample workflow (`.github/workflows/test.yml`):

```yaml
name: Playwright Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        browser: [chromium, firefox, webkit]
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: 17
          distribution: temurin
      - name: Cache Maven packages
        uses: actions/cache@v4
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2-
      - name: Install Playwright browsers
        run: mvn -q exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
      - name: Run tests
        run: mvn -q test -Dbrowser=${{ matrix.browser }}
      - name: Generate Allure report
        run: mvn -q allure:report
      - name: Upload artifacts
        uses: actions/upload-artifact@v4
        with:
          name: test-artifacts
          path: |
            test-results/
            target/site/allure-maven-plugin/
```

## Jenkins / GitLab / Azure
Any CI/CD platform that supports Maven can run this framework.

Basic commands:
```bash
mvn clean test
mvn allure:report
```
