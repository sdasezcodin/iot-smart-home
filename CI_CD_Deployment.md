# CI/CD Pipeline Setup

## Overview

This project uses GitHub Actions for continuous integration and continuous deployment (CI/CD). The pipeline automatically builds, tests, packages, and publishes the application to GitHub Container Registry whenever code is pushed to the main branch.

## Pipeline Structure

```
.github/workflows/ci-cd.yml   # Main pipeline file
Dockerfile                     # Multi-stage containerization
pom.xml                        # Maven build configuration
```

## What the Pipeline Does

### Build Process
1. **Triggers**: Runs on push to main branch, pull requests, or manual dispatch
2. **Environment Setup**: Java 21 with Maven caching for faster builds
3. **Database Services**: DynamoDB Local container for integration testing
4. **Build Steps**: Clean → Compile → Package → Verify JAR
5. **Docker Build**: Multi-stage containerized application build
6. **Image Testing**: Validates Docker image creation and basic functionality

### Deployment Process (Main Branch Only)
7. **Versioning**: Automatic semantic versioning (1.01, 1.02, etc.)
8. **Registry Authentication**: Secure login to GitHub Container Registry
9. **Image Tagging**: Tags with both `latest` and version-specific tags
10. **Publishing**: Pushes Docker images to GitHub Container Registry

## Key Technologies Used

- **GitHub Actions**: Automated CI/CD pipeline with workflow permissions
- **GitHub Container Registry (GHCR)**: Docker image hosting and distribution
- **Java 21**: LTS programming language with Temurin JDK
- **Maven**: Build automation and dependency management
- **DynamoDB Local**: In-memory database for integration testing
- **Docker**: Multi-stage containerization with Alpine Linux base
- **JUnit**: Unit and integration testing framework

## Container Registry

Docker images are automatically published to GitHub Container Registry on successful builds:

- **Registry URL**: `ghcr.io/sdasezcodin/iot-smart-home`
- **Latest Tag**: `ghcr.io/sdasezcodin/iot-smart-home:latest`
- **Versioned Tags**: `ghcr.io/sdasezcodin/iot-smart-home:v1.01`, `v1.02`, etc.

### Pull and Run from Registry

```bash
# Pull the latest image
docker pull ghcr.io/sdasezcodin/iot-smart-home:latest

# Run the application
docker run -it ghcr.io/sdasezcodin/iot-smart-home:latest

# Run with network port exposed
docker run -it -p 5555:5555 ghcr.io/sdasezcodin/iot-smart-home:latest
```

## Local Docker Development

For local development and testing:

```bash
# Build the application image locally
docker build -t iot-smarthome-dashboard:local .

# Run the local build
docker run -it iot-smarthome-dashboard:local

# Run with port mapping for TCP server
docker run -it -p 5555:5555 iot-smarthome-dashboard:local
```

## Common Issues and Solutions

### 1. Container Registry Permission Errors
- **Problem**: `denied: installation not allowed to Create organization package`
- **Solution**: Ensure workflow has `packages: write` permission and repository name matches exactly
- **Check**: Verify GitHub Actions permissions in repository settings

### 2. DynamoDB Connection Issues
- **Problem**: Tests fail because DynamoDB Local connection isn't ready
- **Solution**: Pipeline includes wait logic and health checks for DynamoDB startup
- **Local Testing**: Use `docker run -p 8000:8000 amazon/dynamodb-local:1.21.0`

### 3. Build Failures
- **Problem**: Maven build fails during pipeline run
- **Solution**: Check Java 21 compatibility and dependency versions in `pom.xml`
- **Caching**: Maven dependencies are cached for faster builds

### 4. Docker Multi-Stage Build Issues
- **Problem**: Docker build fails in pipeline
- **Solution**: Test locally with same base images (Maven 3.9.5 + Temurin 21)
- **Security**: Images run as non-root user for production security

### 5. Image Tag Case Sensitivity
- **Problem**: GHCR rejects uppercase characters in image names
- **Solution**: Pipeline automatically converts repository owner to lowercase

## Local Development & Troubleshooting

### Prerequisites Check
```bash
# Check Java version (should be 21)
java --version

# Check Maven version
mvn --version

# Check Docker version
docker --version
```

### Local Build Testing
```bash
# Full Maven build (same as pipeline)
mvn clean package -DskipTests

# Test with DynamoDB Local
docker run -d -p 8000:8000 amazon/dynamodb-local:1.21.0
mvn test

# Test Docker build locally
docker build -t test-build .
docker run -it test-build
```

### Registry Troubleshooting
```bash
# Login to GHCR locally (requires personal access token)
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# Pull specific version
docker pull ghcr.io/sdasezcodin/iot-smart-home:v1.01

# List available tags
curl -s https://api.github.com/users/sdasezcodin/packages/container/iot-smart-home/versions
```

## Pipeline Failure Debugging

### Step-by-Step Debugging Process
1. **Check GitHub Actions**: Go to repository → Actions tab → Click failed workflow
2. **Examine Logs**: Look for specific error messages in each step
3. **Verify Permissions**: Ensure `packages: write` is enabled in workflow
4. **Check Repository Settings**: Actions → General → Workflow permissions
5. **Validate Configuration**: Confirm all files exist and syntax is correct
6. **Test Locally**: Reproduce the issue in local environment

### Quick Health Check
```bash
# Verify all critical files exist
ls -la .github/workflows/ci-cd.yml Dockerfile pom.xml

# Check workflow syntax (if you have act installed)
act --list

# Validate Docker multi-stage build
docker build --target builder -t test-builder .
```

### Environment Variables & Secrets
- **GITHUB_TOKEN**: Automatically provided by GitHub Actions
- **No additional secrets required** for basic functionality
- Container registry uses repository-scoped permissions

---

## Architecture Summary

This CI/CD setup provides:
- ✅ **Automated builds** on code changes
- ✅ **Container registry publishing** with semantic versioning
- ✅ **Multi-stage Docker builds** for optimized images
- ✅ **Integration testing** with DynamoDB Local
- ✅ **Security best practices** with non-root containers
- ✅ **Efficient caching** for faster build times

*Professional CI/CD pipeline ready for production deployment*
