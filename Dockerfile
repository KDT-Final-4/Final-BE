FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /workspace
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle
COPY src src

RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jdk
WORKDIR /app
RUN mkdir -p /app/logs

ARG JAR_FILE=/workspace/build/libs/final-be.jar
COPY --from=builder ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]

