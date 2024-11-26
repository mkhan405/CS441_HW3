import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

// Change to Scala 2.13 since Finch doesn't support Scala 3 yet
ThisBuild / scalaVersion := "2.12.18"

//// More specific dependency conflict resolution
//ThisBuild / libraryDependencySchemes ++= Seq(
//  "io.circe" %% "*" % "early-semver",
//  "org.typelevel" %% "*" % "early-semver"
//)

//// Increase the error level to warn instead of error for version conflicts
//ThisBuild / evictionErrorLevel := Level.Warn

Compile / mainClass := Some("com.khan.api.server.main")

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)


lazy val root = (project in file("."))
  .settings(
    name := "CS441_HW3"
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

//// Force specific versions for problematic transitive dependencies
//dependencyOverrides ++= Seq(
//  "io.circe" %% "circe-jawn" % circeVersion,
//  "io.circe" %% "circe-core" % circeVersion,
//  "io.circe" %% "circe-parser" % circeVersion,
//  "io.circe" %% "circe-generic" % circeVersion
//)

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

  //  "org.scalaj" %% "scalaj-http" % "2.4.2",
//  "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M19"
)

//import scala.collection.Seq
//
//ThisBuild / version := "0.1.0-SNAPSHOT"
//
//ThisBuild / scalaVersion := "2.13.12"
//
//ThisBuild / libraryDependencySchemes += "io.circe" %% "*" % "early-semver"
//
//Compile/mainClass := Some("com.khan.api.main")
//
//lazy val root = (project in file("."))
//  .settings(
//    name := "CS441_HW3"
//  )
//
//resolvers += "Maven Repository" at "https://mvnrepository.com/artifact"
//
//// Configuration Dependencies
//libraryDependencies += "com.typesafe" % "config" % "1.4.3"
//
//// Testing Dependencies
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
//
//// Logging Dependencies
//libraryDependencies ++= Seq(
//  "ch.qos.logback" % "logback-classic" % "1.5.9",  // Logback dependency
//  "org.slf4j" % "slf4j-api" % "2.0.12"             // SLF4J dependency
//)
//
//// Finagle Dependencies
//// https://mvnrepository.com/artifact/com.twitter/finagle-http
//libraryDependencies += "com.twitter" % "finagle-http_2.13" % "24.2.0"
//
//libraryDependencies ++= Seq(
//  "com.github.finagle" % "finch-core_2.13" % "0.34.1",
//  "com.github.finagle" % "finch-circe_2.13" % "0.34.1",
//  // Circe
//  "io.circe" %% "circe-generic" % "0.14.3",
//)