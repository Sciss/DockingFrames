lazy val baseName   = "docking-frames"
lazy val baseNameL  = baseName
lazy val githubName = "DockingFrames"

name := baseName

def basicJavaOpts = Seq("-source", "1.6")

lazy val commonSettings = Seq(
  version            := "0.1.0-SNAPSHOT",
  organization       := "de.sciss",
  scalaVersion       := "2.11.8",
  autoScalaLibrary   := false,
  crossPaths         := false,
  javacOptions                   := basicJavaOpts ++ Seq("-encoding", "utf8", "-Xlint:unchecked", "-target", "1.6"),
  javacOptions in (Compile, doc) := basicJavaOpts,  // doesn't eat `-encoding` or `target`
  description        := "A window docking framework for Swing",
  homepage           := Some(url(s"https://github.com/Sciss/$githubName")),
  licenses           := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt"))
) ++ publishSettings

// ---- publishing ----

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := pomExtraAll
)

lazy val root = Project(id = baseNameL, base = file("."))
  .aggregate(coreProject, commonProject)
  .dependsOn(coreProject, commonProject)
  .settings(commonSettings)
  .settings(
    publishArtifact in (Compile, packageBin) := false, // there are no binaries
    publishArtifact in (Compile, packageDoc) := false, // there are no javadocs
    publishArtifact in (Compile, packageSrc) := false  // there are no sources
  )

lazy val coreProject = Project(id = s"$baseNameL-core", base = file("core"))
  .settings(commonSettings)

lazy val commonProject = Project(id = s"$baseNameL-common", base = file("common"))
  .dependsOn(coreProject)
  .settings(commonSettings)

def pomExtraAll = pomBase ++ pomDevs

def pomBase =
  <scm>
    <url>git@github.com:Sciss/{githubName}.git</url>
    <connection>scm:git:git@github.com:Sciss/{githubName}.git</connection>
  </scm>

def pomDevs =
  <developers>
                <developer>
                        <id>benjamin_sigg@gmx.ch</id>
                        <email>benjamin_sigg@gmx.ch</email>
                        <name>Benjamin Sigg</name>
                        <properties>
                                <credits>the creator</credits>
                        </properties>
                        <roles>
                                <role>owner</role>
                        </roles>
                </developer>
                <developer>
                        <id>Janni Kovacs</id>
                        <email>Janni Kovacs</email>
                        <name>Janni Kovacs</name>
                        <properties>
                                <credits>By finding bugs I didn't imagine that they even could
                                        exist, by asking for features I considered nonsense, by writing the
                                        first StackDockComponent, and by having a critical question for
                                        every change I made.By writing the initial version of the
                                        EclipseTheme.</credits>
                        </properties>
                        <roles>
                                <role>contributor</role>
                        </roles>
                </developer>
                <developer>
                        <id>Ivan Seidl</id>
                        <email>Ivan Seidl</email>
                        <name>Ivan Seidl</name>
                        <properties>
                                <credits>By helping me understand, how hard it is to get into
                                        DockingFrames without proper tutorials or guides. By contributing a
                                        new set of icons for the BubbleTheme.</credits>
                        </properties>
                        <roles>
                                <role>contributor</role>
                        </roles>
                </developer>
                <developer>
                        <id>scrnick</id>
                        <email>scrnick</email>
                        <name>scrnick</name>
                        <properties>
                                <credits>By being the first one writing a non-trivial patch.
                                </credits>
                        </properties>
                        <roles>
                                <role>contributor</role>
                        </roles>
                </developer>
                <developer>
                        <id>Parag Shah</id>
                        <email>Parag Shah</email>
                        <name>Parag Shah</name>
                        <properties>
                                <credits>By contributing a new split-layout-manager (which will be
                                        made available in v1.0.7).</credits>
                        </properties>
                        <roles>
                                <role>contributor</role>
                        </roles>
                </developer>
                <developer>
                        <id>Steffen Kux</id>
                        <email>Steffen Kux</email>
                        <name>Steffen Kux</name>
                        <properties>
                                <credits>Steffen wrote a library producing glass effects, this
                                        library was used by Thomas.</credits>
                        </properties>
                        <roles>
                                <role>contributor</role>
                        </roles>
                </developer>
                <developer>
                        <id>Thomas Hilbert</id>
                        <email>Thomas Hilbert</email>
                        <name>Thomas Hilbert</name>
                        <properties>
                                <credits>Thomas wrote a new fancy tab for the EclipseTheme. He also
                                        provided a new set of icons for the EclipseTheme.</credits>
                        </properties>
                        <roles>
                                <role>contributor</role>
                        </roles>
                </developer>
                <developer>
                        <id>andrei.pozolotin@gmail.com</id>
                        <email>andrei.pozolotin@gmail.com</email>
                        <name>Andrei Pozolotin</name>
                        <properties>
                                <credits>converted project to maven and published to central repo</credits>
                        </properties>
                        <roles>
                                <role>contributor</role>
                        </roles>
                </developer>
                <developer>
                        <id>krijnschaap@gmail.com</id>
                        <email>krijnschaap@gmail.com</email>
                        <name>Krijn Schaap</name>
                        <properties>
                                <credits>Maintenance of maven build system</credits>
                        </properties>
                        <roles>
                                <role>contributor</role>
                        </roles>
                </developer>
    <developer>
      <id>sciss</id>
      <name>Hanns Holger Rutz</name>
      <url>http://www.sciss.de</url>
    </developer>
  </developers>
