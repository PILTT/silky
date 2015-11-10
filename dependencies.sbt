resolvers ++= Seq(
  "Sonatype OSS Releases"            at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots"           at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Public Repositories" at "https://oss.sonatype.org/content/groups/public/"
)

val log4jVersion = "[2.0.1,2.9.99]"
val slf4jVersion = "[1.7.0,1.9.99]"

val slf4j = Seq(
  "org.slf4j" % "slf4j-api" % slf4jVersion % "provided",
  "org.slf4j" % "slf4j-ext" % slf4jVersion % "provided"
)

val log4j = Seq(
  "org.apache.logging.log4j" % "log4j-api"        % log4jVersion % "test",
  "org.apache.logging.log4j" % "log4j-core"       % log4jVersion % "test",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion % "test",
  "com.fasterxml.jackson.core"       % "jackson-databind"        % "2.6.3" % "test",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.6.2" % "test" exclude("org.yaml", "snakeyaml")
)

val testDependencies = Seq(
  "com.github.rhyskeepence" %% "clairvoyance-scalatest" % "[1.0.109,1.0.999]" % "test",
  "org.scalatest" %% "scalatest"   % "3.0.0-M10" % "test",
  "org.scalaz"    %% "scalaz-core" % "7.1.1" % "test"
)

libraryDependencies <++= scalaVersion { scala_version ⇒ Seq(
  "org.scala-lang" % "scala-library" % scala_version,
  "org.scala-lang" % "scala-reflect" % scala_version
) ++
  (CrossVersion.partialVersion(scala_version) match {
    case Some((2, scalaMajor)) if scalaMajor == 10 ⇒ Seq(
      "com.chuusai" % s"shapeless_2.10.5" % "2.1.0",
      compilerPlugin("org.scalamacros" % s"paradise_$scala_version" % "2.0.1")
    )
    case Some((2, scalaMajor)) if scalaMajor >= 11 ⇒ Seq(
      "com.chuusai" %% "shapeless" % "2.1.0",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.3" % "test"
    )
    case _ ⇒ Seq.empty
  })
}
libraryDependencies ++= slf4j ++ log4j ++ testDependencies
