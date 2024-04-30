FROM openjdk:17
ARG JAR_FILE=build/libs/giveme-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ARG ENV
ENV SPRING_PROFILES_ACTIVE=${ENV}
ENTRYPOINT ["java", "-jar", "app.jar"]
