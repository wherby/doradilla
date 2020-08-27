import sbt.Keys.{libraryDependencies, version}
import Dependencies._




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
  ).aggregate(doradillaCore)
  .dependsOn(doradillaCore)

