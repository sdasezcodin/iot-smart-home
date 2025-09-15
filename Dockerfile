# Multi-stage build for IoT Smarthome Dashboard
# Stage 1: Build the application
FROM maven:3.9.5-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml first to leverage Docker layer caching
COPY pom.xml .

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="IoT Smarthome Dashboard"
LABEL version="1.0"
LABEL description="IoT Smarthome Dashboard application"

# Create app user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Create app directory
RUN mkdir -p /app && chown -R appuser:appgroup /app

WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder --chown=appuser:appgroup /app/target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar app.jar

# Switch to non-root user
USER appuser

# Expose port (adjust if your app uses a different port)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD java -cp app.jar com.smarthome.SmartHomeApp --health-check || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]