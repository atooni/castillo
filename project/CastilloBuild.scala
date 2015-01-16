import com.typesafe.sbt.packager.universal.UniversalKeys
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
    libraryDependencies ++= Dependencies.serverDeps
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
    version := "1.0-SNAPSHOT",
    name := "castillo",
    scalaVersion := "2.11.4"
  )

  /**
   * A simple container object for the dependencies.
   */
  object Dependencies {

    lazy val serverDeps = Seq(
      "com.newrelic.agent.java" % "newrelic-agent" % Versions.newRelic
    )
  }
}
