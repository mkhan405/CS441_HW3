package com.khan.gRPCServer

import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import play.api.libs.json._

/**
 * Case class representing the response body returned by the Lambda function.
 *
 * @param generation            The generated text or response.
 * @param prompt_token_count    The number of tokens in the input prompt.
 * @param generation_token_count The number of tokens in the generated response.
 * @param stop_reason           The reason why the generation process stopped (e.g., end-of-sentence).
 */
case class ResponseBody(generation: String, prompt_token_count: Int, generation_token_count: Int,
                        stop_reason: String)

/**
 * Case class representing the full response from the Lambda function.
 *
 * @param body The body of the Lambda response, containing the actual generated data.
 */
case class LambdaResponse(body: ResponseBody)

/**
 * Case class representing the user query sent to the Lambda function.
 *
 * @param prompt The input text prompt provided by the user.
 */
case class UserQuery(prompt: String)

/**
 * Client for interacting with a Lambda function through HTTP requests.
 */
object LambdaClient {
  // JSON readers for deserializing the response from the Lambda function
  private implicit val responseBodyReads: Reads[ResponseBody] = Json.reads[ResponseBody]
  private implicit val lambdaResponseReads: Reads[LambdaResponse] = Json.reads[LambdaResponse]

  // Load application configuration (e.g., the Lambda URL)
  private val config = AppConfig.load()

  // JSON formatter for serializing and deserializing user queries
  implicit val promptRequestFormat: OFormat[UserQuery] = Json.format[UserQuery]

  /**
   * Sends a prompt to the Lambda function and returns the generated response.
   *
   * @param prompt The input text prompt to be sent to the Lambda function.
   * @return The parsed response body containing the generated text and metadata.
   */
  def run(prompt: String): ResponseBody = {
    // Create an HTTP client
    val client = HttpClients.createDefault()
    val request = new HttpPost(config.lambdaUrl)

    // Prepare the request body as JSON
    val requestData = UserQuery(prompt)
    val jsonString: String = Json.toJson(requestData).toString()

    // Set the request body as a JSON string
    request.setEntity(new StringEntity(jsonString))

    // Execute the HTTP request
    val response: CloseableHttpResponse = client.execute(request)

    try {
      // Extract the response content
      val entity = response.getEntity
      val responseString = EntityUtils.toString(entity)
      parseResponse(responseString) // Parse and return the response body
    } finally {
      // Ensure the response and client are properly closed
      response.close()
      client.close()
    }
  }

  /**
   * Parses the JSON string response from the Lambda function into a ResponseBody object.
   *
   * @param jsonString The JSON string received from the Lambda function.
   * @return The parsed ResponseBody object.
   */
  private def parseResponse(jsonString: String): ResponseBody = {
    val jsValue = Json.parse(jsonString)
    jsValue.as[LambdaResponse].body // Deserialize the JSON into a LambdaResponse and extract the body
  }
}
