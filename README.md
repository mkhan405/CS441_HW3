# Homework #2

# Student Information
- First Name: Mohammad Shayan Khan
- Last Name: Khan
- UIN: 667707825
- UIC Email: mkhan405@uic.edu

# Prerequisites
- Ensure Scala 2.12.18 is installed
- Compatible JDK is installed for Scala 2 (e.g. JDK 11)
- Docker and Docker Compose installed

# Installation

- Clone repository from Github
```sh
git clone git@github.com:mkhan405/CS441_HW3.git
```
- Install SBT dependencies by:
```sh
sbt update
```
- Build protobuf dependencies by running:
```sh
sbt compile
```

## Running Services Locally (without Docker)
- To run the relevant services without docker, change the gRPC host from `grpc_server` to `127.0.0.1` in
    `gRPCServer/resources/application.conf`:
```
app {
    port = 8081
    gRPCHost = "grcp_server" <- Change this to "127.0.0.1"
    gRPCPort = 50051
}
```
- Uncomment the local URL for the `lambdaUrl` for local testing in 
  `finchServer/resources/application.conf`:
```
app {
    port = 50051
    lambdaUrl = "https://tulxy06dt8.execute-api.us-east-1.amazonaws.com/default/" <- Comment this
    ;lambdaUrl = "https://3qvzoc1esg.execute-api.us-east-1.amazonaws.com/tempStage/" <- Uncomment this
}
```
- Navigate to the gRPC project and start the gRPC Server:
```sh
cd gRPCServer/
sbt run
```
- In another terminal session, start the finch server from the project root directory:
```sh
cd ../            # Navigate to root
cd finchServer
sbt run
```

## Running Services with Docker

- Uncomment the local URL for the `lambdaUrl` for local testing in
  `finchServer/resources/application.conf`:
```
app {
    port = 50051
    lambdaUrl = "https://tulxy06dt8.execute-api.us-east-1.amazonaws.com/default/" <- Comment this
    ;lambdaUrl = "https://3qvzoc1esg.execute-api.us-east-1.amazonaws.com/tempStage/" <- Uncomment this
}
```
- Generate the jar files for each project by running this command in the root directory of the project:
```sh
sbt clean assembly
```
- Utilize Docker Compose to run containerized microservices
```sh
docker compose up
docker compose up -d # To run in detached mode
```

