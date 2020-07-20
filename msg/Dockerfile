FROM java:8-jdk-alpine
COPY  target/msg-0.0.1-SNAPSHOT-jar-with-dependencies.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "msg-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]