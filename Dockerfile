# base image for Apple M1 chip
FROM arm64v8/openjdk:17-jdk-slim
COPY target/shopRESTfulAPI-0.0.1-SNAPSHOT.jar shopRESTfulAPI-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "/shopRESTfulAPI-1.0.0.jar"]
