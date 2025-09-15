# Multi-stage build for IoT Smart Home Dashboard
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 5555

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]