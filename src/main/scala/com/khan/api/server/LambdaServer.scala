package com.khan.api.server

import com.khan.api.LambdaClient
import io.grpc.{Server, ServerBuilder}
import com.khan.proto.query.{GenerationResult, QueryRequest, TextGenerationServiceGrpc}

import scala.concurrent.{ExecutionContext, Future}

object LambdaServer {
  def main(args: Array[String]): Unit = {
    val server = new LambdaServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()
  }
}

class LambdaServer(executionContext: ExecutionContext) { self =>
  private[this] var server: Server = null
  val port = 50051

  private def start(): Unit = {
    println(s"Started gRPC Server on: ${port}")
    server = ServerBuilder
      .forPort(port)
      .addService(
        TextGenerationServiceGrpc.bindService(
          new TextGenerationImpl,
          executionContext
        )
      ).asInstanceOf[ServerBuilder[_]].build.start

    sys.addShutdownHook {
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