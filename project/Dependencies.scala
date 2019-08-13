import com.typesafe.sbt.SbtNativePackager.autoImport.maintainer
import com.typesafe.sbt.SbtPgp.autoImportImpl.useGpg
import sbt.Keys._
import sbt.{url, _}

/**
  * For  in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/31
  */
object Dependencies {
  lazy val akka = "2.5.22"
  /* dependencies */
  val commonDependencies = Seq (
    // -- Logging --
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    // -- Akka --
    "com.typesafe.akka" %% "akka-actor"   % akka,
    "com.typesafe.akka" %% "akka-slf4j"   % akka,
    "com.typesafe.akka" %% "akka-cluster" % akka,
    "com.typesafe.akka" %% "akka-cluster-tools" % akka,
    "com.typesafe.akka" %% "akka-testkit" % akka,
    // https://mvnrepository.com/artifact/org.scalatest/scalatest
    "org.scalatest" %% "scalatest" % "3.1.0-RC1" % Test,
    // https://mvnrepository.com/artifact/com.typesafe.play/play-json
    "com.typesafe.play" %% "play-json" % "2.6.13",
    // https://mvnrepository.com/artifact/com.datastax.cassandra/cassandra-driver-core
    "com.datastax.cassandra" % "cassandra-driver-core" % "3.7.1"
  )
  

  lazy val commonSettings = Seq(
    organization := "io.github.wherby",
    scalaVersion := "2.12.7",
    version := "1.3.8",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    libraryDependencies ++= commonDependencies,
    maintainer := "wherby <187225577@qq.com>",
    licenses := Seq("Apache License 2.0" -> url("https://github.com/wherby/doradilla/blob/master/LICENSE")),
    //useGpg := true,
    homepage := Some(url("https://github.com/wherby/doradilla")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/wherby/doradilla.git"),
        "scm:git@github.com:wherby/doradilla.git"
      )
    ),
    developers := List(
      Developer(
        id    = "wherby",
        name  = "Tao Zhou",
        email = "187225577@qq.com",
        url   = url("https://github.com/wherby")
      )
    ),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.contains("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
  )

  lazy val settings = Seq(
    parallelExecution in Test := false,
    fork in run := false,   //###If the value is true, Ctrl + C may only kill JVM and not kill Akka. Set to false to kill togother.  Set to true for publishing in poor network.
    // These options will be used for *all* versions.
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-encoding", "UTF-8",
      "-Xlint",
    ),
  )
}
