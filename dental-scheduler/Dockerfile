FROM openjdk:17-jdk-slim as builder
ARG JAR_FILE=target/dental-scheduler-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} application.jar
EXPOSE 8090
ENTRYPOINT ["java","-Dspring.profiles.active=docker", "-jar", "/application.jar"]