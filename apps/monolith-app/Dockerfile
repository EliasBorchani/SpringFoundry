# Use OpenJDK 21 base image
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy everything
COPY . .

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Build your Spring Boot jar (skip tests for speed)
RUN ./gradlew :apps:monolith-app:bootJar --no-daemon -x test

# Set JAR path
ARG JAR_FILE=apps/monolith-app/build/libs/monolith-app.jar

# Copy built jar to a clean location
COPY $JAR_FILE app.jar

# Expose port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","/app/app.jar"]
