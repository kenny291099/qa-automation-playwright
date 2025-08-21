# 📁 Project Structure

```text
qa-automation-playwright/
├── allure-report/                 # Generated Allure reports
├── logs/                          # Test execution logs
├── src/
│   ├── main/java/com/saucedemo/   # Example implementation
│   │   ├── config/                # Configuration interfaces
│   │   ├── pages/                 # Page Object Model classes
│   │   └── utils/                 # Utility classes
│   └── test/java/com/saucedemo/tests/  # Test classes
│       └── resources/             # Config, logging, Allure props
├── target/                        # Maven build artifacts
├── test-results/                  # Screenshots, videos, traces
├── pom.xml                        # Maven config
└── README.md
```

This structure follows best practices for **Page Object Model** and separation of concerns.
