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
    name := "Doradilla-core",
    publishArtifact := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.contains("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  )

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings: _*)
  .aggregate(doradillaCore)
  .settings(
    name := "Doradilla",
    libraryDependencies ++= Seq("io.github.wherby" %% "doradilla-core" % "0.1.0-SNAPSHOT"),
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))),
  )

