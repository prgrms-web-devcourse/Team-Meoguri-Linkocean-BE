FROM openjdk:11-jdk

ARG JAR_FILE=/build/libs/linkocean-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/app.jar"]
