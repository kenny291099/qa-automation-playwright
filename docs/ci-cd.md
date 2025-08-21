# ⚙️ CI/CD Integration

This framework is designed to integrate seamlessly with modern CI/CD platforms and provides comprehensive automation capabilities for continuous testing.

## GitHub Actions

### Features
- **Automated Test Execution**: Run tests on PRs, merges, and scheduled intervals
- **Cross-Browser Testing**: Matrix strategy supporting Chromium, Firefox, and WebKit
- **Artifact Collection**: Automatic collection of logs, screenshots, videos, and traces
- **Report Generation**: Automated Allure report generation and publishing
- **Parallel Execution**: Optimized test execution across multiple browsers
- **Caching**: Maven dependency caching for faster build times

### Sample Workflow

Create `.github/workflows/playwright-tests.yml`:

```yaml
name: Playwright Test Suite
on: 
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]
  schedule:
    # Run tests daily at 2 AM UTC
    - cron: '0 2 * * *'
  workflow_dispatch:
    inputs:
      browser:
        description: 'Browser to test'
        required: false
        default: 'chromium'
        type: choice
        options:
        - chromium
        - firefox
        - webkit
        - all

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      fail-fast: false
      matrix:
        browser: [chromium, firefox, webkit]
    
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
        
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: 17
          distribution: temurin
          
      - name: Cache Maven dependencies
        uses: actions/cache@v4
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2-
          
      - name: Install Playwright browsers
        run: mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
        
      - name: Run tests
        run: mvn clean test -Dbrowser=${{ matrix.browser }} -Dheadless=true
        env:
          BROWSER: ${{ matrix.browser }}
          
      - name: Generate Allure report
        if: always()
        run: mvn allure:report
        
      - name: Upload test artifacts
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-artifacts-${{ matrix.browser }}
          path: |
            test-results/
            target/site/allure-maven-plugin/
            logs/
            target/allure-results/
          retention-days: 30
          
      - name: Upload Allure report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: allure-report-${{ matrix.browser }}
          path: target/site/allure-maven-plugin/
          retention-days: 30

  publish-results:
    needs: test
    runs-on: ubuntu-latest
    if: always()
    steps:
      - name: Download all artifacts
        uses: actions/download-artifact@v4
        
      - name: Publish Test Results
        uses: dorny/test-reporter@v1
        if: always()
        with:
          name: Playwright Test Results
          path: '**/target/surefire-reports/*.xml'
          reporter: java-junit
```

## Jenkins Integration

### Jenkinsfile Example
```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chromium', 'firefox', 'webkit'],
            description: 'Browser for test execution'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run tests in headless mode'
        )
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Install Dependencies') {
            steps {
                sh 'mvn clean compile'
                sh 'mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"'
            }
        }
        
        stage('Run Tests') {
            steps {
                sh """
                    mvn test \
                    -Dbrowser=${params.BROWSER} \
                    -Dheadless=${params.HEADLESS}
                """
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                sh 'mvn allure:report'
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/allure-maven-plugin',
                    reportFiles: 'index.html',
                    reportName: 'Allure Test Report'
                ])
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'test-results/**/*', allowEmptyArchive: true
                archiveArtifacts artifacts: 'logs/**/*', allowEmptyArchive: true
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}
```

## GitLab CI/CD

### .gitlab-ci.yml Example
```yaml
image: maven:3.9-openjdk-17

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"

cache:
  paths:
    - .m2/repository/

stages:
  - test
  - report

before_script:
  - mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"

test:chromium:
  stage: test
  script:
    - mvn clean test -Dbrowser=chromium -Dheadless=true
  artifacts:
    when: always
    reports:
      junit: target/surefire-reports/*.xml
    paths:
      - test-results/
      - target/allure-results/
    expire_in: 1 week

test:firefox:
  stage: test
  script:
    - mvn clean test -Dbrowser=firefox -Dheadless=true
  artifacts:
    when: always
    reports:
      junit: target/surefire-reports/*.xml
    paths:
      - test-results/
      - target/allure-results/
    expire_in: 1 week

generate_report:
  stage: report
  script:
    - mvn allure:report
  artifacts:
    paths:
      - target/site/allure-maven-plugin/
    expire_in: 1 month
  dependencies:
    - test:chromium
    - test:firefox
```

## Azure DevOps

### azure-pipelines.yml Example
```yaml
trigger:
  branches:
    include:
    - main
    - develop

pool:
  vmImage: 'ubuntu-latest'

variables:
  MAVEN_CACHE_FOLDER: $(Pipeline.Workspace)/.m2/repository

stages:
- stage: Test
  jobs:
  - job: RunTests
    strategy:
      matrix:
        Chromium:
          browserName: 'chromium'
        Firefox:
          browserName: 'firefox'
        WebKit:
          browserName: 'webkit'
    steps:
    - task: JavaToolInstaller@0
      inputs:
        versionSpec: '17'
        jdkArchitectureOption: 'x64'
        jdkSourceOption: 'PreInstalled'
        
    - task: Cache@2
      inputs:
        key: 'maven | "$(Agent.OS)" | pom.xml'
        restoreKeys: |
          maven | "$(Agent.OS)"
        path: $(MAVEN_CACHE_FOLDER)
        
    - script: |
        mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
      displayName: 'Install Playwright browsers'
      
    - script: |
        mvn clean test -Dbrowser=$(browserName) -Dheadless=true
      displayName: 'Run tests'
      
    - task: PublishTestResults@2
      inputs:
        testResultsFormat: 'JUnit'
        testResultsFiles: 'target/surefire-reports/*.xml'
        testRunTitle: 'Playwright Tests - $(browserName)'
      condition: always()
      
    - script: |
        mvn allure:report
      displayName: 'Generate Allure report'
      condition: always()
      
    - task: PublishBuildArtifacts@1
      inputs:
        pathToPublish: 'test-results'
        artifactName: 'test-artifacts-$(browserName)'
      condition: always()
```

## Docker Integration

### Dockerfile for CI/CD
```dockerfile
FROM mcr.microsoft.com/playwright/java:v1.40.0-jammy

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .
COPY src ./src

# Install dependencies
RUN mvn dependency:resolve

# Install Playwright browsers
RUN mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"

# Run tests
CMD ["mvn", "clean", "test"]
```

## Best Practices

### Environment Configuration
- Use environment variables for sensitive data
- Implement different configurations for different environments
- Use secrets management for credentials

### Parallel Execution
```bash
# Optimize parallel execution
mvn test -Dparallel.workers=4 -Dfailsafe.forkCount=2

# Control resource usage
mvn test -Xmx4g -XX:MaxMetaspaceSize=1g
```

### Artifact Management
- Archive test results, screenshots, videos, and traces
- Set appropriate retention policies
- Use meaningful artifact names with timestamps

### Notification Setup
- Configure build status notifications
- Set up Slack/Teams/Email alerts for failures
- Include test report links in notifications

## Monitoring & Analytics

### Test Metrics Collection
- Track test execution times
- Monitor pass/fail rates
- Analyze flaky test patterns
- Generate trend reports

### Performance Monitoring
- Monitor resource usage during test execution
- Track CI/CD pipeline performance
- Optimize build and test execution times

This CI/CD setup ensures reliable, scalable, and maintainable automated testing across multiple environments and browsers.
