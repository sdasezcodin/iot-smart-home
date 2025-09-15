# CI/CD Pipeline Setup

## Overview

This project uses GitHub Actions for continuous integration and continuous deployment (CI/CD). The pipeline automatically builds, tests, and packages the application whenever code is pushed.

## Pipeline Structure

```
.github/workflows/ci-cd.yml   # Main pipeline file
Dockerfile                     # For containerization
pom.xml                        # Maven build configuration
```

## What the Pipeline Does

1. **Triggers**: Runs automatically on code push or pull requests
2. **Build Steps**: Validates → Compiles → Tests → Packages → Creates Docker image
3. **Testing**: Uses DynamoDB Local for database testing
4. **Artifacts**: Saves JAR files and test reports
5. **Docker**: Builds containerized version of the application

## Key Technologies Used

- **GitHub Actions**: For automated CI/CD
- **Java 21**: Programming language
- **Maven**: Build tool
- **DynamoDB**: Database (local version for testing)
- **Docker**: Containerization
- **JUnit**: Testing framework

## Docker Setup

The project includes Docker support for easy deployment:

```bash
# Build the application image
docker build -t iot-dashboard .

# Run the application
docker run -it iot-dashboard

# Run with network port exposed
docker run -it -p 5555:5555 iot-dashboard
```

## Common Issues and Solutions

### 1. DynamoDB Connection Issues
- **Problem**: Tests fail because DynamoDB connection isn't working
- **Solution**: Make sure test credentials are set properly

### 2. Build Failures
- **Problem**: Maven build fails during pipeline run
- **Solution**: Check that Java 21 is specified in the workflow

### 3. Docker Issues
- **Problem**: Docker build fails in pipeline
- **Solution**: Test Docker build locally first to verify Dockerfile

## Basic Troubleshooting

```bash
# Check Java version
java --version

# Test Maven build locally
mvn clean package

# Test Docker build locally
docker build -t test-build .
```

## What to Check if Pipeline Fails

1. Go to GitHub Actions tab in repository
2. Click on the failed workflow
3. Examine the error messages
4. Check that all configuration files exist
5. Verify Java and Maven versions

---

*This is a simplified guide for interview explanation purposes*
