FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle gradle/
COPY src ./src

RUN chmod +x gradlew

RUN ./gradlew build --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]