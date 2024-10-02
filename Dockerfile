# 1. Base image
FROM openjdk:17-jdk-slim AS build

# 2. Set the working directory
WORKDIR /app

# 3. Copy build.gradle and settings.gradle
COPY build.gradle .
COPY settings.gradle .

# 4. Copy the source code
COPY src ./src

# 5. Build the application
RUN ./gradlew build --no-daemon

# 6. Create a new image for the application
FROM openjdk:17-jre-slim

# 7. Set the working directory
WORKDIR /app

# 8. Copy the jar file from the previous stage
COPY --from=build /app/build/libs/*.jar app.jar

# 9. Expose the port
EXPOSE 8080

# 10. Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]