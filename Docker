FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

# Copy gradle files for dependency resolution
COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY buildSrc buildSrc

# Run gradle to download dependencies (will be cached in Docker layers)
RUN ./gradlew --no-daemon dependencies

# Copy source code
COPY src src

# Build the application
RUN ./gradlew --no-daemon :bootJar

# Extract layers for better caching
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user to run the application
RUN addgroup -S akita && adduser -S akita -G akita

# Set necessary environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

# Copy the built JAR from the build stage
COPY --from=build /workspace/app/akita/build/libs/akita-*.jar /app/akita.jar

# Set ownership to the non-root user
RUN chown -R akita:akita /app

# Switch to non-root user
USER akita

# Expose the application port
EXPOSE 8080

# Run the application with health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/akita.jar"]
