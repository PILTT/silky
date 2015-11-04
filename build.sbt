import scala.util.Try

name         := "silky"
organization := "com.github.piltt"
version      := Try(sys.env("BUILD_NUMBER")).map("1.0." + _).getOrElse("1.0-SNAPSHOT")

scalaVersion := "2.10.6"
crossScalaVersions := Seq("2.10.6", "2.11.7")

javacOptions  ++= Seq("-Xms512m", "-Xmx512m", "-Xss4m")
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-language:implicitConversions")

graphSettings
