FROM gradle:latest AS build
WORKDIR /home/gradle/app
COPY settings.docker.gradle.kts ./settings.gradle.kts
COPY gradle.properties ./
COPY backend/build.gradle.kts ./backend/
COPY gradle ./gradle
RUN gradle dependencies --stacktrace
COPY backend/src/ ./backend/src
RUN gradle buildFatJar --no-daemon

FROM amazoncorretto:22 AS runtime
WORKDIR /app
COPY --from=build /home/gradle/app/backend/build/libs/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]