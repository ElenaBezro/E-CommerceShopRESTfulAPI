# base image
FROM eclipse-temurin:17-jdk-focal
COPY target/shopRESTfulAPI-0.0.1-SNAPSHOT.jar shopRESTfulAPI-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "/shopRESTfulAPI-1.0.0.jar"]
