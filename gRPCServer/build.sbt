ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"


ThisBuild / libraryDependencySchemes ++= Seq(
  "io.circe" %% "*" % "early-semver",
  "org.typelevel" %% "*" % "early-semver"
)

ThisBuild / evictionErrorLevel := Level.Warn

Compile / mainClass := Some("com.khan.gRPCServer.LambdaServer")

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

resolvers ++= Seq(
  "Maven Repository" at "https://mvnrepository.com/artifact",
  "Maven Central" at "https://repo1.maven.org/maven2/"
)

val circeVersion = "0.14.3"
val finchVersion = "0.31.0"


libraryDependencies ++= Seq(
  // Configuration
  "com.typesafe" % "config" % "1.4.3",

  // Testing
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,

  // Logging
  "ch.qos.logback" % "logback-classic" % "1.5.9",
  "org.slf4j" % "slf4j-api" % "2.0.12",

  "com.github.finagle" % "finch-core_2.12" % "0.31.0",
  "com.github.finagle" % "finch-circe_2.12" % "0.31.0",
  "io.circe" % "circe-generic_2.12" % "0.9.0",

  // Netty dependencies
  "io.netty" % "netty-all" % "4.1.100.Final",
  "io.netty" % "netty-transport-native-epoll" % "4.1.100.Final" classifier "linux-x86_64",
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.26",
  "com.typesafe.akka" %% "akka-http" % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
  "com.twitter" %% "twitter-server" % "22.12.0",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "io.netty" % "netty-tcnative-boringssl-static" % "2.0.58.Final",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "org.apache.httpcomponents" % "httpclient" % "4.5.13"
)