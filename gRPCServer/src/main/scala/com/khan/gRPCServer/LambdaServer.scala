package com.khan.gRPCServer

import com.khan.proto.query.{GenerationResult, QueryRequest, TextGenerationServiceGrpc}
import io.grpc.{Server, ServerBuilder}

import scala.concurrent.{ExecutionContext, Future}
import org.slf4j.{Logger, LoggerFactory}

object LambdaServer {
  def main(args: Array[String]): Unit = {
    val server = new LambdaServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()
  }
}

class LambdaServer(executionContext: ExecutionContext) { self =>
  val logger: Logger = LoggerFactory.getLogger("gRPCServer")
  val config: AppConfig = AppConfig.load()

  // Set as a mutable var with initialization of server being handled in the
  // start method
  private[this] var server: Server = null

  private def start(): Unit = {
    logger.info(s"Started gRPC Server on: ${config.port}")
    server = ServerBuilder
      .forPort(config.port)
      .addService(
        TextGenerationServiceGrpc.bindService(
          new TextGenerationImpl,
          executionContext
        )
      ).asInstanceOf[ServerBuilder[_]].build.start

    sys.addShutdownHook {
      logger.info("Shutting down gRPC Server...")
      self.stop()
    }
  }

  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }

  private class TextGenerationImpl extends TextGenerationServiceGrpc.TextGenerationService {
    override def generateResponse(request: QueryRequest): Future[GenerationResult] = {
      val lambdaResponse = LambdaClient.run(request.queryText)
      val reply = GenerationResult(lambdaResponse.generation, lambdaResponse.prompt_token_count,
        lambdaResponse.generation_token_count, lambdaResponse.stop_reason)
      Future.successful(reply)
    }
  }
}