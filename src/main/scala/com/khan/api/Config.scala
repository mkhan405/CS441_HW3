//package com.khan.api
//
//import com.typesafe.config.{Config, ConfigFactory}
//
//case class GRPCConfig(host: String, port: Int, content_type: String, accept: String)
//case class LambdaConfig(url: String)
//case class AppConfig(port: Int, gRPCConfig: GRPCConfig, lambdaConfig: LambdaConfig)
//
//object AppConfig {
//  private val config: Config = ConfigFactory.load()
//
//  def load(): AppConfig = {
//    AppConfig(
//      port = config.getInt("app.port"),
//      gRPCConfig = GRPCConfig(
//        host = config.getString("app.gRPCConfig.host"),
//        port = config.getInt("app.gRPCConfig.port"),
//        content_type = config.getString("app.gRPCConfig.content-type"),
//        accept = config.getString("app.gRPCConfig.accept")
//      ),
//      lambdaConfig = LambdaConfig(config.getString("app.lambdaConfig.url"))
//    )
//  }
//}