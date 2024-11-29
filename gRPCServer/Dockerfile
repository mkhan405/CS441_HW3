FROM openjdk:11-jre-slim

RUN mkdir -p /app
WORKDIR /app

COPY ./target/scala-2.12/gRPCServer-assembly-0.1.0-SNAPSHOT.jar ./

EXPOSE 50051
ENTRYPOINT ["java", "-jar", "gRPCServer-assembly-0.1.0-SNAPSHOT.jar"]