# 🐞 Troubleshooting

## Common Issues

### 1. Playwright browsers not installed
```bash
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
```

### 2. Tests failing in headless mode
```bash
mvn test -Dheadless=false
```

### 3. Slow test execution
```bash
mvn test -Dslow.mo=1000
```

### 4. Allure port conflicts
```bash
mvn allure:serve -Dallure.serve.port=8080
```

## Debug Mode
Enable advanced debug:
```bash
mvn test -Dtrace.mode=ALWAYS -Dvideo.mode=ALWAYS -Dheadless=false
```
