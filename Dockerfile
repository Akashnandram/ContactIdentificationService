# Use Maven with Java 17 for build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy project files and build the application
COPY . .
RUN mvn clean package -DskipTests

# Use JRE with Java 17 for final image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/ContactIdentificationService-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
