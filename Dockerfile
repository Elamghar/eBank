# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/ebanking-backend.jar app.jar

# Expose port
EXPOSE 8085

# Run with explicit port configuration
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-Dserver.port=8085", "-jar", "app.jar"]
