FROM gradle:7.3-jdk11 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build --no-daemon
COPY build/libs/user-service-0.0.1-SNAPSHOT.jar app.jar

FROM openjdk:11.0.2-jre-slim
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/app.jar ./
CMD java -Djava.security.egd=file:/dev/urandom -jar app.jar -Dspring-boot.run.profiles=docker -Xms512m -Xmx4G
