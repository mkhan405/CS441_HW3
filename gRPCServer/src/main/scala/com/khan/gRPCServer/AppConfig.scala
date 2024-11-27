package com.khan.gRPCServer

import com.typesafe.config.{Config, ConfigFactory}

case class AppConfig(port: Int, lambdaUrl: String)

object AppConfig {
  private val config: Config = ConfigFactory.load()

  def load(): AppConfig = {
    AppConfig(
      port = config.getInt("app.port"),
      lambdaUrl = config.getString("app.lambdaUrl"),
    )
  }
}