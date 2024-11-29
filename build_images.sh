sbt clean assembly

cd gRPCServer
docker build --no-cache -t grpc_server .
cd finchServer
docker build --no-cache -t finch_server .