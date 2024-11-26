package com.khan.api

import org.apache.http.impl.client.HttpClients
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import play.api.libs.json._

case class ResponseBody(generation: String, prompt_token_count: Int, generation_token_count: Int,
                        stop_reason: String)
case class LambdaResponse(body: ResponseBody)
case class UserQuery(prompt: String)

object LambdaClient {
  private val config: LambdaConfig = AppConfig.load().lambdaConfig
  private implicit val responseBodyReads: Reads[ResponseBody] = Json.reads[ResponseBody]
  private implicit val lambdaResponseReads: Reads[LambdaResponse] = Json.reads[LambdaResponse]

  implicit val promptRequestFormat: OFormat[UserQuery] = Json.format[UserQuery]

  def run(prompt: String): ResponseBody = {
    val url = config.url
    val client = HttpClients.createDefault()
    val request = new HttpPost(url)


    // Set the request body (assuming `requestBody` is a JSON string)
    val requestData = UserQuery(prompt)
    val jsonString: String = Json.toJson(requestData).toString()

    print(jsonString)

    request.setEntity(new StringEntity(jsonString))

    // Set the appropriate headers for JSON content
    // request.setHeader("Content-Type", "application/json")
//    request.setHeader("Accept", "application/json")

    val response: CloseableHttpResponse = client.execute(request)

    try {
      val entity = response.getEntity
      val responseString = EntityUtils.toString(entity)
      print(responseString)
      parseResponse(responseString)

    } finally {
      response.close()
      client.close()
    }
  }

  private def parseResponse(jsonString: String): ResponseBody = {
    val jsValue = Json.parse(jsonString)
    jsValue.as[LambdaResponse].body // Convert the JsValue into a list of Posts
  }


//
//  def run(prompt: String) = {
//    val response: Response[String] = quickRequest.get(uri"${config.url}").send()
//    response.body
//  }
}

//
//import com.khan.proto.query.{QueryRequest, GenerationResult}
//import scalaj.http.Http
//
//object LambdaClient {
//  val config = AppConfig.load()
//  val grpcConfig = config.gRPCConfig
//
//  def request(queryRequest: QueryRequest) = {
//    println(queryRequest.toByteArray.mkString("Array(", ", ", ")"))
//    val request = Http("https://cx60os4och.execute-api.us-east-1.amazonaws.com/llmServerStage/chat")
//      .headers(Map(
//        "Content-Type" -> grpcConfig.content_type,
//        "Accept" -> grpcConfig.accept
//      ))
//      .postData(queryRequest.toByteArray)
//
//    val response = request.asBytes.body
//    print(request.asString)
//    val generationResult: GenerationResult = GenerationResult.parseFrom(response)
//    generationResult
//  }
//}
