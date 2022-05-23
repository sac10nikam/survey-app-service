FROM adoptopenjdk/openjdk11:alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} survey-app-service.jar
ENTRYPOINT ["java","-jar","survey-app-service.jar"]