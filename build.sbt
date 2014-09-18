import scala.util.Try

name := "silky"

organization := "com.github.piltt"

version := Try(sys.env("BUILD_NUMBER")).map("1.0." + _).getOrElse("1.0-SNAPSHOT")

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.2")

javacOptions ++= Seq("-Xms512m", "-Xmx512m", "-Xss4m")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-language:implicitConversions")

resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Public Repositories" at "https://oss.sonatype.org/content/groups/public/"
)

graphSettings

libraryDependencies <<= scalaVersion { scala_version ⇒ Seq(
    "org.scala-lang" % "scala-library" % scala_version,
    "org.slf4j" % "slf4j-api" % "1.7.7",
    "org.slf4j" % "slf4j-ext" % "1.7.7",
    "org.apache.logging.log4j" % "log4j-api" % "2.0.1" % "test",
    "org.apache.logging.log4j" % "log4j-core" % "2.0.1" % "test",
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.0.1" % "test",
    "com.lmax" % "disruptor" % "3.2.1" % "test",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test",
    "com.github.rhyskeepence" %% "clairvoyance-scalatest" % "1.0.93" % "test"
  ) ++ (
    CrossVersion.partialVersion(scala_version) match {
      case Some((2, scalaMajor)) if scalaMajor >= 11 ⇒ Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.2")
      case _ ⇒ Seq.empty
    }
  )
}

sonatypeSettings

publishTo <<= version { project_version ⇒
  val nexus = "https://oss.sonatype.org/"
  if (project_version.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ ⇒ false }

credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", System.getenv("SONATYPE_USER"), System.getenv("SONATYPE_PASSWORD"))

pomExtra :=
  <url>https://github.com/PILTT/silky</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git://github.com/PILTT/silky.git</url>
    <connection>scm:git://github.com/PILTT/silky.git</connection>
  </scm>
  <developers>
    <developer>
      <id>franckrasolo</id>
      <name>Franck Rasolo</name>
      <url>https://github.com/franckrasolo</url>
    </developer>
  </developers>
