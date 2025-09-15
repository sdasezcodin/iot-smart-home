# CI/CD Pipeline Setup & Troubleshooting Guide

## 🚀 Overview

This document provides comprehensive information about setting up and troubleshooting the CI/CD pipeline for the IoT Smart Home Dashboard project using GitHub Actions and GitHub Codespaces.

## 📁 Pipeline Structure

```
.github/
└── workflows/
    └── ci-cd.yml              # Main CI/CD pipeline

.devcontainer/
├── devcontainer.json         # Codespaces configuration  
└── setup.sh                  # Development environment setup script
```

## 🔧 GitHub Actions Pipeline

### Pipeline Features

- **Automatic Triggers**: Runs on push/PR to `main` branch + manual workflow dispatch
- **Multi-stage Build**: Validation → Compilation → Testing → Packaging → Docker Build
- **DynamoDB Integration**: Uses DynamoDB Local for testing
- **Artifact Management**: Uploads JAR files and test reports
- **Docker Support**: Builds and tests Docker images

### Key Improvements Made

1. **Updated Action Versions**:
   - `actions/checkout@v4`
   - `actions/setup-java@v4` 
   - `actions/cache@v4`
   - `actions/upload-artifact@v4`

2. **Enhanced DynamoDB Health Check**:
   - Uses `wget` instead of `curl` for better reliability
   - Added `health-start-period` for proper initialization
   - Improved connection verification

3. **Better Caching**:
   - Built-in Maven caching in `setup-java@v4`
   - Enhanced cache paths for better performance

4. **Environment Configuration**:
   - Test AWS credentials for DynamoDB Local
   - Environment variables for testing
   - Proper Maven configuration

5. **Enhanced Testing**:
   - Step-by-step build process
   - JAR verification
   - Docker image testing
   - Test result artifacts

## 🌐 GitHub Codespaces Setup

### Development Environment

- **Base Image**: `mcr.microsoft.com/devcontainers/java:21-jdk`
- **Features**: Docker, AWS CLI, GitHub CLI
- **Extensions**: Java Pack, Maven, Docker tools
- **Port Forwarding**: 5555 (App Server), 8000 (DynamoDB Local)

### Quick Start Commands

After opening in Codespaces, use these aliases:

```bash
build              # mvn clean package
test               # mvn test  
run                # java -jar target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar
docker-build       # docker build -t iot-smarthome-dashboard .
start-dynamodb     # Start DynamoDB Local on port 8000
```

## 🐛 Common Issues & Solutions

### Issue 1: Pipeline Fails on DynamoDB Health Check

**Symptoms**:
```
Error: Health check failed
```

**Solutions**:
1. **Check Service Configuration**:
   ```yaml
   services:
     dynamodb:
       image: amazon/dynamodb-local:latest
       options: >-
         --health-cmd "wget --no-verbose --tries=1 --spider http://localhost:8000/ || exit 1"
         --health-start-period 10s
   ```

2. **Verify DynamoDB Connection**:
   ```bash
   curl -s http://localhost:8000/
   ```

### Issue 2: Maven Build Failures

**Symptoms**:
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

**Solutions**:
1. **Check Java Version**:
   ```yaml
   - uses: actions/setup-java@v4
     with:
       distribution: 'temurin'
       java-version: '21'
   ```

2. **Validate Dependencies**:
   ```bash
   mvn dependency:resolve
   mvn dependency:tree
   ```

### Issue 3: Test Failures with AWS/DynamoDB

**Symptoms**:
```
software.amazon.awssdk.core.exception.SdkClientException: Unable to load AWS credentials
```

**Solutions**:
1. **Set Test Credentials**:
   ```yaml
   env:
     AWS_ACCESS_KEY_ID: test
     AWS_SECRET_ACCESS_KEY: test
     AWS_DEFAULT_REGION: us-east-1
     DYNAMODB_ENDPOINT: http://localhost:8000
   ```

2. **Configure AWS CLI**:
   ```bash
   aws configure set aws_access_key_id test --profile default
   aws configure set aws_secret_access_key test --profile default
   aws configure set region us-east-1 --profile default
   ```

### Issue 4: Docker Build Failures

**Symptoms**:
```
ERROR: failed to solve: process "/bin/sh -c mvn clean package" did not complete successfully
```

**Solutions**:
1. **Check Dockerfile**:
   ```dockerfile
   FROM maven:3.9.11-eclipse-temurin-21 AS build
   WORKDIR /app
   COPY pom.xml .
   RUN mvn dependency:go-offline
   COPY src ./src
   RUN mvn clean package
   ```

2. **Verify Dependencies**:
   - Ensure all dependencies are available
   - Check for network connectivity issues in build

### Issue 5: Artifact Upload Failures

**Symptoms**:
```
Error: Artifact path is not valid
```

**Solutions**:
1. **Verify File Paths**:
   ```bash
   ls -la target/
   find . -name "*.jar" -type f
   ```

2. **Update Upload Configuration**:
   ```yaml
   - uses: actions/upload-artifact@v4
     with:
       name: iot-app-jar
       path: ./target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar
       retention-days: 5
   ```

### Issue 6: Codespaces Environment Issues

**Symptoms**:
- Java/Maven not found
- Missing dependencies
- Port forwarding not working

**Solutions**:
1. **Rebuild Container**: Use "Codespaces: Rebuild Container" command
2. **Check Setup Script**: Ensure `.devcontainer/setup.sh` runs successfully
3. **Manual Installation**:
   ```bash
   # Re-run setup manually if needed
   bash .devcontainer/setup.sh
   
   # Check Java installation
   java --version
   mvn --version
   ```

## 🔍 Debugging Commands

### Pipeline Debugging

```bash
# Local testing commands
mvn validate                    # Validate project
mvn clean compile              # Compile only  
mvn dependency:tree            # Check dependencies
mvn surefire-report:report     # Generate test report

# Test DynamoDB connection
curl -I http://localhost:8000/

# Build Docker locally
docker build -t test-build .
docker run --rm test-build --help
```

### Environment Debugging

```bash
# Check environment
echo $JAVA_HOME
echo $MAVEN_HOME
echo $PATH

# Check AWS configuration  
aws configure list
aws dynamodb list-tables --endpoint-url http://localhost:8000

# Check ports
netstat -tlnp | grep :8000
netstat -tlnp | grep :5555
```

## 📊 Pipeline Monitoring

### Success Metrics
- ✅ Build completes in < 5 minutes
- ✅ All 243 tests pass
- ✅ JAR artifact created successfully
- ✅ Docker image builds without errors
- ✅ No security vulnerabilities detected

### Performance Benchmarks
- **Checkout**: ~10 seconds
- **Java Setup**: ~20 seconds  
- **Dependency Cache**: ~30 seconds (first run), ~5 seconds (cached)
- **Maven Build**: ~60-90 seconds
- **Tests**: ~45-60 seconds
- **Docker Build**: ~120-180 seconds

## 🛠️ Advanced Configuration

### Custom Environment Variables

Add these to your repository secrets:

```yaml
# Production deployment (if needed)
AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
AWS_REGION: ${{ secrets.AWS_REGION }}

# Docker registry (if needed)
DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
```

### Multi-Environment Support

```yaml
strategy:
  matrix:
    java-version: [17, 21]
    os: [ubuntu-latest, macos-latest]
```

### Parallel Testing

```yaml
jobs:
  test:
    strategy:
      matrix:
        test-group: [unit, integration, performance]
```

## 🚀 Deployment Extensions

### AWS ECS Deployment

```yaml
- name: Deploy to ECS
  if: github.ref == 'refs/heads/main'
  run: |
    aws ecs update-service \
      --cluster smart-home-cluster \
      --service smart-home-service \
      --force-new-deployment
```

### Docker Hub Publishing

```yaml
- name: Push to Docker Hub
  if: github.ref == 'refs/heads/main'
  run: |
    echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
    docker push your-username/iot-smarthome-dashboard:${{ github.sha }}
```

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitHub Codespaces Documentation](https://docs.github.com/en/codespaces)
- [AWS DynamoDB Local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Docker Documentation](https://docs.docker.com/)

## ❓ Need Help?

1. **Check Pipeline Logs**: Go to Actions tab in GitHub repository
2. **Review Failed Steps**: Click on failed job to see detailed logs
3. **Local Testing**: Use the debugging commands above
4. **Create Issue**: Open a GitHub issue with:
   - Pipeline run URL
   - Error messages
   - Steps to reproduce
   
## 🎯 Next Steps

After resolving current issues:

1. **Add Security Scanning**: Include SAST/DAST tools
2. **Performance Testing**: Add load testing steps
3. **Code Coverage**: Integrate coverage reporting
4. **Notification Setup**: Add Slack/email notifications
5. **Environment Promotion**: Set up staging/production pipelines

---

*Last updated: $(date)*