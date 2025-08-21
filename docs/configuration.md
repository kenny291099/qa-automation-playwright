# ⚙️ Configuration

## Properties File (`src/test/resources/config.properties`)
```properties
# Application settings
base.url=https://www.saucedemo.com
browser=chromium
headless=true
timeout=30000

# Evidence
video.mode=ON_FAILURE
screenshot.mode=ON_FAILURE
trace.mode=ON_FAILURE

# Execution
parallel.workers=1
retry.count=1
slow.mo=0
```

## Override with System Properties
```bash
mvn test -Dbrowser=firefox -Dheadless=false -Dtimeout=60000
```
