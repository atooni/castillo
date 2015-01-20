import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbt.web.Import._
import com.typesafe.sbt.less.Import._
import play._
import sbt._
import Keys._


object CastilloBuild extends Build with UniversalKeys {

  val appName = "castillo"

  /**
   * Define the root project as an aggregation of the subprojects.
   */

  lazy val root =
    project.in(file("."))
    .settings(commonSettings:_*)
    .aggregate(server)

  /**
   * Define the server project as a Play application.
   */
  lazy val server =
    project.in(file("server"))
    .settings(serverSettings:_*)
    .settings(sharedDirSettings:_*)
    .enablePlugins(PlayScala)

  /**
   * The settings for the server module
   */
  lazy val serverSettings = commonSettings ++ Seq(
    name := s"$appName-server",
    libraryDependencies ++= Dependencies.serverDeps,
    includeFilter in (Assets, LessKeys.less) := "__main.less"
  )

  /**
   * Here we collect the settings for the shared source directories.
   */
  lazy val sharedDirSettings = Seq(
    unmanagedSourceDirectories in Compile += 
      baseDirectory.value / "shared" / "main" / "scala"
  )

  /**
   * The setting that will be applied to all sub projects
   */
  lazy val commonSettings = Seq(
    organization := "de.woq",
    version := Versions.app,
    name := "castillo",
    scalaVersion := Versions.scala
  )

  /**
   * A simple container object for the dependencies.
   */
  object Dependencies {

    lazy val serverDeps = Seq(
      "com.newrelic.agent.java" % "newrelic-agent" % Versions.newRelic,
      "org.webjars" % "bootstrap" % Versions.bootstrap,
      "org.scalatestplus" %% "play" % Versions.scalaTestPlus % "test"
    )
  }
}
