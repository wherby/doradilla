import com.typesafe.sbt.SbtNativePackager.autoImport.maintainer
import sbt.Keys._
import sbt._

/**
  * For  in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/31
  */
object Dependencies {
  lazy val akka = "2.5.21"
  /* dependencies */
  val commonDependencies = Seq (
    // -- Logging --
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    // -- Akka --
    "com.typesafe.akka" %% "akka-actor"   % akka,
    "com.typesafe.akka" %% "akka-slf4j"   % akka,
    "com.typesafe.akka" %% "akka-cluster" % akka,
    "com.typesafe.akka" %% "akka-testkit" % akka % "test",
    // https://mvnrepository.com/artifact/org.scalatest/scalatest
    "org.scalatest" %% "scalatest" % "3.1.0-RC1" % Test,
    // https://mvnrepository.com/artifact/com.typesafe.play/play-json
    "com.typesafe.play" %% "play-json" % "2.6.13"
  )

  lazy val commonSettings = Seq(
    organization := "io.github.wherby",
    scalaVersion := "2.12.7",
    version := "0.1.0-SNAPSHOT",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    libraryDependencies ++= commonDependencies,
    maintainer := "wherby <187225577@qq.com>",
  )

  lazy val settings = Seq(
    parallelExecution in Test := false,
    fork in run := false,   //###If the value is true, Ctrl + C may only kill JVM and not kill Akka. Set to false to kill togother.
    // These options will be used for *all* versions.
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-encoding", "UTF-8",
      "-Xlint",
    ),
  )
}
