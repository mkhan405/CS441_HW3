package com.khan.gRPCServer

import com.khan.proto.query.{GenerationResult, QueryRequest, TextGenerationServiceGrpc}
import io.grpc.{Server, ServerBuilder}

import scala.concurrent.{ExecutionContext, Future}
import org.slf4j.{Logger, LoggerFactory}

/**
 * The `LambdaServer` object initializes and starts the gRPC server.
 */
object LambdaServer {

  /**
   * Main entry point for the gRPC server.
   *
   * @param args Command-line arguments (not used).
   */
  def main(args: Array[String]): Unit = {
    // Create and start the server, using the global execution context
    val server = new LambdaServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown() // Block the main thread until the server shuts down
  }
}

/**
 * The `LambdaServer` class encapsulates the logic for creating, starting, and stopping
 * a gRPC server that handles text generation requests.
 *
 * @param executionContext The execution context to be used for asynchronous operations.
 */
class LambdaServer(executionContext: ExecutionContext) { self =>

  // Logger for the server
  val logger: Logger = LoggerFactory.getLogger("gRPCServer")

  // Configuration object loaded from application settings
  val config: AppConfig = AppConfig.load()

  // Mutable variable to hold the server instance
  private[this] var server: Server = null

  /**
   * Starts the gRPC server and binds the `TextGenerationService`.
   */
  private def start(): Unit = {
    logger.info(s"Started gRPC Server on: ${config.port}")

    server = ServerBuilder
      .forPort(config.port) // Configure the port from the application config
      .addService(
        TextGenerationServiceGrpc.bindService(
          new TextGenerationImpl, // Service implementation for handling requests
          executionContext
        )
      ).asInstanceOf[ServerBuilder[_]].build.start // Build and start the server

    // Add a shutdown hook to gracefully stop the server on JVM termination
    sys.addShutdownHook {
      logger.info("Shutting down gRPC Server...")
      self.stop()
    }
  }

  /**
   * Stops the gRPC server, if it is running.
   */
  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  /**
   * Blocks the main thread until the gRPC server shuts down.
   */
  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }

  /**
   * Implementation of the gRPC `TextGenerationService`.
   * Handles incoming requests for text generation.
   */
  private class TextGenerationImpl extends TextGenerationServiceGrpc.TextGenerationService {

    /**
     * Handles the `generateResponse` RPC call.
     *
     * @param request The request containing the input query text.
     * @return A `Future` containing the generated response.
     */
    override def generateResponse(request: QueryRequest): Future[GenerationResult] = {
      // Call the LambdaClient to get the generated response
      val lambdaResponse = LambdaClient.run(request.queryText)

      // Construct the gRPC response
      val reply = GenerationResult(
        lambdaResponse.generation,
        lambdaResponse.prompt_token_count,
        lambdaResponse.generation_token_count,
        lambdaResponse.stop_reason
      )

      // Return the response as a successful future
      Future.successful(reply)
    }
  }
}
