# Multi-stage Dockerfile for Spring Boot backend with hot-reload support

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and checkstyle configuration
COPY pom.xml checkstyle.xml ./

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage with hot-reload
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install Maven for hot-reload support (using Debian package manager)
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy pom.xml for dev dependencies
COPY pom.xml .
COPY src ./src

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run with Spring Boot DevTools for hot-reload
CMD ["java", "-jar", "-Dspring.devtools.restart.enabled=true", "app.jar"]
