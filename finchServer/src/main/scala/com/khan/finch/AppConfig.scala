package com.khan.finch

import com.typesafe.config.{Config, ConfigFactory}

case class AppConfig(port: Int, gRPCHost: String, gRPCPort: Int)

object AppConfig {
  private val config: Config = ConfigFactory.load()

  def load(): AppConfig = {
    AppConfig(
      port = config.getInt("app.port"),
      gRPCHost = config.getString("app.gRPCHost"),
      gRPCPort = config.getInt("app.gRPCPort")
    )
  }
}