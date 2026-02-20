# Stage 1: Build the jar
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copy everything needed to build the app
COPY . .

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the Spring Boot jar
RUN ./gradlew :apps:monolith-app:bootJar --no-daemon -x test

# Stage 2: Clean runtime image
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy only the jar from the builder stage
COPY --from=builder /app/apps/monolith-app/build/libs/monolith-app.jar app.jar

# Expose port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","/app/app.jar"]
