FROM gradle:7.5.1-jdk17 as builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle/

COPY src src/

RUN gradle clean build -x test


FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

ARG JAR_FILE=target/*.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar", "/app/app.jar"]