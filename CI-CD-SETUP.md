# CI/CD Pipeline Setup & Troubleshooting Guide

## 🚀 Overview

This document provides comprehensive information about setting up and troubleshooting the CI/CD pipeline for the IoT Smart Home Dashboard project using GitHub Actions.

## 📁 Pipeline Structure

```
.github/
└── workflows/
    └── ci-cd.yml              # Main CI/CD pipeline
Dockerfile                     # Container build configuration
pom.xml                        # Maven project configuration
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

## 🐳 Docker Configuration

### Dockerfile Features

- **Multi-stage Build**: Optimized build process with separate build and runtime stages
- **Base Images**: 
  - Build: `maven:3.9.5-eclipse-temurin-21`
  - Runtime: `eclipse-temurin:21-jre-alpine`
- **Security**: Non-root user execution
- **Optimization**: Layer caching for dependencies
- **Health Checks**: Built-in container health monitoring

### Local Docker Commands

```bash
# Build the Docker image
docker build -t iot-smarthome-dashboard:latest .

# Run the container
docker run -d -p 8080:8080 iot-smarthome-dashboard:latest

# View container logs
docker logs <container-id>

# Stop the container
docker stop <container-id>
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
1. **Check Dockerfile Exists**:
   - Ensure `Dockerfile` is present in the project root
   - Verify the Dockerfile uses the correct base images

2. **Local Testing**:
   ```bash
   # Test Docker build locally
   docker build -t test-build .
   
   # Check if image was created
   docker images | grep test-build
   ```

3. **Verify Dependencies**:
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

### Issue 6: JAR File Not Found

**Symptoms**:
```
Error: Unable to access jarfile target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar
```

**Solutions**:
1. **Verify Maven Build**:
   ```bash
   mvn clean package
   ls -la target/
   ```

2. **Check Maven Shade Plugin**:
   - Ensure the shade plugin is configured in `pom.xml`
   - Verify the main class is correctly specified

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
docker run --rm test-build
```

### Local Environment Debugging

```bash
# Check Java/Maven installation
java --version
mvn --version

# Check build artifacts
ls -la target/
find . -name "*.jar" -type f

# Test application locally
java -jar target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar

# Check Docker installation
docker --version
docker images
```

## 📊 Pipeline Monitoring

### Success Metrics
- ✅ Build completes in < 8 minutes
- ✅ All tests pass
- ✅ JAR artifact created successfully
- ✅ Docker image builds without errors
- ✅ Artifacts uploaded successfully

### Performance Benchmarks
- **Checkout**: ~10 seconds
- **Java Setup**: ~20 seconds  
- **DynamoDB Setup**: ~40 seconds
- **Maven Build**: ~60-90 seconds
- **Docker Build**: ~180-300 seconds (multi-stage)
- **Artifact Upload**: ~10-20 seconds

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
- [AWS DynamoDB Local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Docker Documentation](https://docs.docker.com/)
- [Docker Multi-stage Builds](https://docs.docker.com/develop/dev-best-practices/)

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