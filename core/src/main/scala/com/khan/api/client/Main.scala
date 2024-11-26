package com.khan.api.client

import com.khan.api.{AppConfig, LambdaClient}
import com.khan.proto.query.{GenerationResult, QueryRequest, TextGenerationServiceGrpc}
import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import io.grpc.{ManagedChannel, ManagedChannelBuilder}


case class UserRequest(prompt: String)
case class UserResponse(generation: String, prompt_token_count: Int, generation_token_count: Int, stop_reason: String)

object Main extends TwitterServer {
  // Load in Application Configs
  val config: AppConfig = AppConfig.load()
  // Make gRPC Channel
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress(config.gRPCConfig.host, config.gRPCConfig.port)
    .usePlaintext().asInstanceOf[ManagedChannelBuilder[_]].build

  // Define multiple endpoints
  val ping: Endpoint[String] = get("hello") {
    Ok("Hello World")
  }

  val query: Endpoint[UserResponse] = post("query" :: jsonBody[UserRequest]) { request: UserRequest =>
      val queryRequest = QueryRequest(request.prompt)
      val blockingStub = TextGenerationServiceGrpc.blockingStub(channel)
      val reply: GenerationResult = blockingStub.generateResponse(queryRequest)
      Ok(UserResponse(reply.generation, reply.generationTokenCount.toInt, reply.promptTokenCount.toInt,
        reply.stopReason))
  }

  // Start the server
  println(s"Server starting on port ${config.port}...")

  def main(): Unit = {
    val server = Http.server.serve(s":${config.port}", (ping :+: query).toService)

    onExit {
      server.close()
    }

    Await.ready(server)
  }
}