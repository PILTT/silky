import scala.util.Try

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

credentials += Credentials(
  realm = "Sonatype Nexus Repository Manager",
  host  = "oss.sonatype.org",
  System.getenv("SONATYPE_USER"),
  System.getenv("SONATYPE_PASSWORD")
)

pgpPassphrase := Some(Try(sys.env("SECRET")).getOrElse("goaway").toCharArray)
pgpSecretRing := file("./scripts/sonatype.asc")

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
