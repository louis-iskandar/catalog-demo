FROM openjdk:17-jdk-slim
LABEL maintainer="li@all4dev.de"

# Add the application's jar to the container
ARG JAR_FILE=target/catalog-app.jar
ADD ${JAR_FILE} app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]