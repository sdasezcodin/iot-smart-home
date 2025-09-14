# Stage 1: Build the project inside Docker.
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package

# Stage 2: Run the app
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy the runnable JAR from the build stage
COPY --from=build /app/target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
