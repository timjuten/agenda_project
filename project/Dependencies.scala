import sbt._

object Dependencies {
  lazy val Zio = "dev.zio" %% "zio" % "2.0.0"
  lazy val ZioCli = "dev.zio" %% "zio-cli" % "0.4.0"
  lazy val Munit = "org.scalameta" %% "munit" % "1.0.2"
  lazy val Sqlite = "org.xerial" % "sqlite-jdbc" % "3.47.0.0"
  lazy val TypeSafeConfig = "com.typesafe" % "config" % "1.4.3"
  lazy val OsLib = "com.lihaoyi" %% "os-lib" % "0.11.3"
  // lazy val Slf4j = "org.slf4j" % "slf4j-nop" % "2.0.16"
}
