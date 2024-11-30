package com.khan.finch

import com.typesafe.config.{Config, ConfigFactory}

/**
 * Case class representing the application configuration settings.
 *
 * @param port The port number the Finch server will run on.
 * @param gRPCHost The hostname or IP address of the gRPC service.
 * @param gRPCPort The port number the gRPC service is running on.
 */
case class AppConfig(port: Int, gRPCHost: String, gRPCPort: Int)

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
      port = config.getInt("app.port"),         // Retrieve the server port setting from the config
      gRPCHost = config.getString("app.gRPCHost"), // Retrieve the gRPC host setting from the config
      gRPCPort = config.getInt("app.gRPCPort")    // Retrieve the gRPC port setting from the config
    )
  }
}
