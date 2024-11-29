ThisBuild / version := "0.1.0-SNAPSHOT"

// Change to Scala 2.13 since Finch doesn't support Scala 3 yet
ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .aggregate(core, finchServer, gRPCServer)

lazy val core = (project in file("core"))
  .settings(
    name := "core"
  )

lazy val finchServer = (project in file("finchServer"))
  .settings(
    name := "finchServer"
  )

lazy val gRPCServer = (project in file("gRPCServer"))
  .settings(
    name := "gRPCServer"
  )

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", "services", xs@_*) => MergeStrategy.filterDistinctLines
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}
