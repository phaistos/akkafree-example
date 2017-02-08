import sbt._
import sbt.Keys._

object AkkafreeBuild extends Build {

  lazy val akkafree = Project(
    id = "akkafree",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "akkafree",
      organization := "org.example",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.11.8"
      // add other settings here
    )
  )
}
