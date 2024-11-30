import org.scalatest.funsuite.AnyFunSuite
import io.finch._
import io.circe.generic.auto._
import io.finch.circe._
import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.util.Await
import com.khan.finch.Main
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.khan.finch.{StatsResponse, UserResponse}

import play.api.libs.json._

class FinchTest extends AnyFunSuite {

  private implicit val userResponseReads: Reads[UserResponse] = Json.reads[UserResponse]
  private implicit val statsResponseReads: Reads[StatsResponse] = Json.reads[StatsResponse]

  // Start the Finch server before tests
  val server: ListeningServer = Http.server.serve(":8081", (Main.ping :+: Main.query :+: Main.getStats).toService)
  val client: Service[Request, Response] = Http.client.newService("localhost:8081")

  // Ensure server stops after tests
  def afterAll(): Unit = {
    Await.result(server.close())
  }

  test("Ping endpoint should return 'Hello World'") {
    val request = Request(Method.Get, "/hello")

    val response = Await.result(client(request))
    assert(response.status == Status.Ok)
    assert(response.contentString == "\"Hello World\"")
  }

  test("Stats endpoint should be return default values at the start") {
    val request = Request(Method.Get, "/stats")
    val response = Await.result(client(request))

    val jsValue = Json.parse(response.contentString)
    val body = jsValue.as[StatsResponse]
    assert(response.status == Status.Ok)
    assert(body.num_of_questions == 0)
    assert(body.total_prompt_tokens == 0)
    assert(body.total_generation_tokens == 0)
  }

  test("Query endpoint should handle a valid UserRequest") {
    val request = Request(Method.Post, "/query")
    request.contentString = """{"prompt": "Hello World"}"""
    request.contentType = "application/json"

    val response = Await.result(client(request))
    val jsValue = Json.parse(response.contentString)
    val body = jsValue.as[UserResponse]
    assert(response.status == Status.Ok)
    assert(body.generation != null)
    assert(body.generation_token_count != 0)
    assert(body.prompt_token_count != 0)
    assert(body.stop_reason != null)
  }

  test("Stats endpoint should be updated after questions") {
    val request = Request(Method.Get, "/stats")
    val response = Await.result(client(request))

    val jsValue = Json.parse(response.contentString)
    val body = jsValue.as[StatsResponse]
    assert(response.status == Status.Ok)
    assert(body.num_of_questions != 0)
    assert(body.total_prompt_tokens != 0)
    assert(body.total_generation_tokens != 0)
  }


  test("Stats endpoint should be updated for each successive question") {
    val llmRequest = Request(Method.Post, "/query")
    llmRequest.contentString = """{"prompt": "Hello World"}"""
    llmRequest.contentType = "application/json"

    val llmResponse = Await.result(client(llmRequest))
    assert(llmResponse.status == Status.Ok)

    val llmJsValue = Json.parse(llmResponse.contentString)
    val llmBody = llmJsValue.as[UserResponse]

    val request = Request(Method.Get, "/stats")
    val response = Await.result(client(request))

    val jsValue = Json.parse(response.contentString)
    val body = jsValue.as[StatsResponse]
    assert(response.status == Status.Ok)
    assert(body.num_of_questions == 2)
    assert(body.total_prompt_tokens - llmBody.prompt_token_count != 0)
    assert(body.total_generation_tokens - llmBody.generation_token_count != 0)
  }
}
