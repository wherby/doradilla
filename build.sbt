import sbt.Keys.{libraryDependencies, version}
import Dependencies._



//ThisBuild / scalaVersion := scala213

publishMavenStyle := true
releaseEarlyWith in Global := SonatypePublisher

pgpPublicRing := file("./travis/local.pubring.asc")
pgpSecretRing := file("./travis/local.secring.asc")

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
    publishArtifact := false,
    mainClass  := Some("io.github.wherby.doradilla.app.SimpleClusterApp"),//object with,
  ).aggregate(doradillaCore,docs)
  .dependsOn(doradillaCore,docs)

// Define a special test task which does not fail when any test fails,
// so sequential tasks (like SonarQube analysis) will be performed no matter the test result.
lazy val ciTests = taskKey[Unit]("Run tests for CI")

ciTests := {
  // Capture the test result
  val testResult = (test in Test).result.value
}