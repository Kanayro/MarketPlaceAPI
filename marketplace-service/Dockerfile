FROM gradle:7.5.1-jdk17 as builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle/

COPY src src/

RUN gradle build -x test

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar", "app.jar"]