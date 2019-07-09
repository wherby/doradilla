import sbt.Keys.{libraryDependencies, version}
import Dependencies._




version in Docker := "latest"

dockerExposedPorts in Docker := Seq(1600)

dockerEntrypoint in Docker := Seq("sh", "-c", "bin/clustering $*")

dockerRepository := Some("wherby")

dockerBaseImage := "java"




lazy val doradillaCore = (project in file("doradilla-core"))
  .settings(commonSettings: _*)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "doradilla-core",
    publishArtifact := true,

  )

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings: _*)
  .settings(
    name := "doradilla",
    publishArtifact := true,
    mainClass  := Some("io.github.wherby.doradilla.app.SimpleClusterApp"),//object with,
  ).aggregate(doradillaCore)
  .dependsOn(doradillaCore)

