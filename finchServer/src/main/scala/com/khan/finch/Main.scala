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

/**
 * Case class to represent a user query request with a prompt.
 *
 * @param prompt The text prompt provided by the user.
 */
case class UserRequest(prompt: String)

/**
 * Case class to represent the response to a user query.
 *
 * @param generation The generated text.
 * @param prompt_token_count The number of tokens in the prompt.
 * @param generation_token_count The number of tokens in the generated text.
 * @param stop_reason The reason for stopping the generation (e.g., max tokens reached).
 */
case class UserResponse(generation: String, prompt_token_count: Int, generation_token_count: Int, stop_reason: String)

/**
 * Case class to represent statistics about the current session, including the number of queries
 * and token counts.
 *
 * @param num_of_questions The total number of questions asked.
 * @param total_prompt_tokens The total number of tokens in all prompts.
 * @param total_generation_tokens The total number of tokens in all generated responses.
 */
case class StatsResponse(num_of_questions: Int, total_prompt_tokens: Int, total_generation_tokens: Int)

/**
 * Main object for running the Finch server. This server handles user queries and provides session statistics.
 */
object Main extends TwitterServer {

  // Logger for the server
  val log: Logger = LoggerFactory.getLogger("finchServer")

  // Load in Application Configs from the configuration file
  val config: AppConfig = AppConfig.load()

  // Create a gRPC channel to communicate with the gRPC service
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress(config.gRPCHost, config.gRPCPort)
    .usePlaintext().asInstanceOf[ManagedChannelBuilder[_]].build

  // Array to keep track of session statistics:
  // stats[0] -> Number of questions
  // stats[1] -> Total Prompt Tokens
  // stats[2] -> Total Generation Tokens
  val stats: Array[Int] = Array(0, 0, 0)

  /**
   * Ping endpoint to test connectivity.
   *
   * @return A "Hello World" response indicating the server is running.
   */
  val ping: Endpoint[String] = get("hello") {
    Ok("Hello World")
  }

  /**
   * Query endpoint to submit a user query to the Lambda service via gRPC.
   *
   * @param request A `UserRequest` containing the prompt text.
   * @return A `UserResponse` containing the generated text and token counts.
   */
  val query: Endpoint[UserResponse] = post("query" :: jsonBody[UserRequest]) { request: UserRequest =>
    // Create the QueryRequest for the gRPC service
    val queryRequest = QueryRequest(request.prompt)

    // Create a blocking gRPC stub to send the query to the Lambda service
    val blockingStub = TextGenerationServiceGrpc.blockingStub(channel)

    // Send the request and get the response
    val reply: GenerationResult = blockingStub.generateResponse(queryRequest)

    // Update session statistics
    stats(0) += 1
    stats(1) += reply.generationTokenCount.toInt
    stats(2) += reply.promptTokenCount.toInt

    // Return the generated response as a UserResponse
    Ok(UserResponse(reply.generation, reply.generationTokenCount.toInt, reply.promptTokenCount.toInt, reply.stopReason))
  }

  /**
   * Stats endpoint to retrieve the current session statistics.
   *
   * @return A `StatsResponse` containing the number of questions, total prompt tokens,
   *         and total generation tokens in the current session.
   */
  val getStats: Endpoint[StatsResponse] = get("stats") {
    Ok(StatsResponse(stats(0), stats(1), stats(2)))
  }

  // Log the server startup
  log.info(s"Starting finch server on port ${config.port}...")

  /**
   * Main entry point for the Finch server. It starts the server and handles incoming requests.
   */
  def main(): Unit = {
    // Start the Finch server and handle the defined routes
    val server = Http.server.serve(s":${config.port}", (ping :+: query :+: getStats).toService)

    // Define the shutdown behavior
    onExit {
      log.info("Exiting finch server...")
      server.close()
    }

    // Keep the server running
    Await.ready(server)
  }
}
