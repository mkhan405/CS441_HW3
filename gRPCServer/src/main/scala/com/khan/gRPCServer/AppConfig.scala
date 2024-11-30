package com.khan.gRPCServer

import com.typesafe.config.{Config, ConfigFactory}

/**
 * Case class representing the application configuration settings for the gRPC server.
 *
 * @param port The port number the gRPC server will run on.
 * @param lambdaUrl The URL of the Lambda service that the gRPC server will interact with.
 */
case class AppConfig(port: Int, lambdaUrl: String)

object AppConfig {
  // Load the application configuration from the resources (typically from `application.conf`)
  private val config: Config = ConfigFactory.load()

  /**
   * Loads the configuration values and returns an `AppConfig` instance.
   *
   * This method reads the necessary configuration settings from the application config file
   * and returns an instance of `AppConfig` containing the parsed values.
   *
   * @return An instance of `AppConfig` with values loaded from the config file.
   */
  def load(): AppConfig = {
    AppConfig(
      port = config.getInt("app.port"),           // Retrieve the server port setting from the config
      lambdaUrl = config.getString("app.lambdaUrl") // Retrieve the Lambda URL setting from the config
    )
  }
}
