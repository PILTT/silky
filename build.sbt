organization := "silky"

name := "silky"

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.2")

resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Public Repositories" at "https://oss.sonatype.org/content/groups/public/"
)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "org.slf4j" % "slf4j-ext" % "1.7.7",
  "org.apache.logging.log4j" % "log4j-api" % "2.0" % "test",
  "org.apache.logging.log4j" % "log4j-core" % "2.0" % "test",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.0" % "test",
  "com.lmax" % "disruptor" % "3.2.1" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.github.rhyskeepence" %% "clairvoyance-scalatest" % "1.0.56" % "test"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")
