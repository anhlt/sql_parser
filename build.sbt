ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "scala-project",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.12.0",
      "org.scalatest" %% "scalatest" % "3.2.17" % Test
    ),
    // Ensure Java 21 compatibility
    javacOptions ++= Seq("-source", "11", "-target", "11"),
    scalacOptions ++= Seq("-release", "11")
  )
ThisBuild / scalaVersion     := "2.13.14"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "3.0.2"
)

ThisBuild / organization := "com.example"
