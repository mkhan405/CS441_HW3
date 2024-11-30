package com.khan.finch

import com.khan.proto.query.{GenerationResult, QueryRequest, TextGenerationServiceGrpc}

import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import org.slf4j.{Logger, LoggerFactory}


case class UserRequest(prompt: String)
case class UserResponse(generation: String, prompt_token_count: Int, generation_token_count: Int, stop_reason: String)
case class StatsResponse(num_of_questions: Int, total_prompt_tokens: Int, total_generation_tokens: Int)

object Main extends TwitterServer {
  val log: Logger = LoggerFactory.getLogger("finchServer")
  // Load in Application Configs
  val config: AppConfig = AppConfig.load()
  // Make gRPC Channel
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress(config.gRPCHost, config.gRPCPort)
    .usePlaintext().asInstanceOf[ManagedChannelBuilder[_]].build

  // Array to keep track of session statistics
  // stats[0] -> Number of questions
  // stats[1] -> Total Prompt Tokens
  // stats[3] -> Total Generation Tokens
  val stats: Array[Int] = Array(0, 0, 0)

  // Ping - test connectivity
  val ping: Endpoint[String] = get("hello") {
    Ok("Hello World")
  }

  // Query - Submit user query to lambda via gRPC
  val query: Endpoint[UserResponse] = post("query" :: jsonBody[UserRequest]) { request: UserRequest =>
      val queryRequest = QueryRequest(request.prompt)
      val blockingStub = TextGenerationServiceGrpc.blockingStub(channel)
      val reply: GenerationResult = blockingStub.generateResponse(queryRequest)
      stats(0) += 1
      stats(1) += reply.generationTokenCount.toInt
      stats(2) += reply.promptTokenCount.toInt
      Ok(UserResponse(reply.generation, reply.generationTokenCount.toInt, reply.promptTokenCount.toInt,
        reply.stopReason))
  }

  // Stats - Retrieve stats from the current session
  val getStats: Endpoint[StatsResponse] = get("stats") {
    Ok(StatsResponse(stats(0), stats(1), stats(2)))
  }

  // Start the server
  log.info(s"Starting finch server on port ${config.port}...")


  def main(): Unit = {
    val server = Http.server.serve(s":${config.port}", (ping :+: query :+: getStats).toService)

    onExit {
      log.info("Exiting finch server...")
      server.close()
    }

    Await.ready(server)
  }
}