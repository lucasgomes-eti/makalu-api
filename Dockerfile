FROM gradle:9.2.1-jdk21-alpine AS build
WORKDIR /app
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src
RUN gradle build --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ARG PROFILE
ENTRYPOINT ["java", "-jar", "app.jar --spring.profiles.active=$PROFILE"]
