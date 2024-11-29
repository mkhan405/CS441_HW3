FROM openjdk:11-jre-slim

RUN mkdir -p /app
WORKDIR /app

COPY ./target/scala-2.12/finchServer-assembly-0.1.0-SNAPSHOT.jar ./

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "finchServer-assembly-0.1.0-SNAPSHOT.jar"]