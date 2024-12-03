import Dependencies.*

ThisBuild / scalaVersion := "2.13.15"
ThisBuild / version := "0.1.1-SNAPSHOT"
ThisBuild / organization := "nl.rasom.workshop"
ThisBuild / organizationName := "Scala Workshop"

lazy val agenda = (project in file("."))
  .settings(
    name := "agenda",
    libraryDependencies += Zio,
    libraryDependencies += ZioCli,
    libraryDependencies += Sqlite,
    libraryDependencies += TypeSafeConfig,
    libraryDependencies += OsLib,
    libraryDependencies += Munit % Test
  )
