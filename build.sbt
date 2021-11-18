import sbt.Keys.{libraryDependencies, version}
import Dependencies._



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




// https://github.com/djspiewak/sbt-github-actions
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches +=
  RefPredicate.StartsWith(Ref.Tag("v"))

//ThisBuild / crossScalaVersions := supportedScalaVersion

ThisBuild / githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release")))

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

inThisBuild(List(
  organization := "org.doradilla",
  homepage := Some(url("https://github.com/wherby/doradilla")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "wherby",
      "Tao Zhou",
      "187225577@qq.com",
      url("https://github.com/wherby/dora")
    )
  )
))



//docs build

import Dependencies.commonSettings

lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "document for doradilla",
    paradoxTheme := Some(builtinParadoxTheme("generic")),
    paradoxIllegalLinkPath := raw".*\\.md".r,
    paradoxProperties in Compile ++=Map("project.description" -> "Description for doradilla library.",
      "github.base_url" -> s"https://github.com/wherby/doradilla/tree/v${version.value}")
  )


// Define task to  copy html files
val copyDocs = taskKey[Unit]("Copy html files from src/main/html to cross-version target directory")

// Implement task
copyDocs := {
  import Path._

  val src = baseDirectory.value  /"docs" /"target" / "paradox"/"site"/ "main"

  val dest = baseDirectory.value /"public" /"docs"
  IO.delete(dest)
  dest.mkdir()
  // Copy files to source files to target
  IO.copyDirectory(src,dest)
}
