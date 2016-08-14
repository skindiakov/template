import java.nio.file.{StandardCopyOption, Files}

import Dependencies._

scalaVersion in Global := "2.11.7"

lazy val copyRes = TaskKey[Unit]("copyRes")


lazy val main = (project in file("main")).
  configs(IntegrationTest).
  settings(libraryDependencies ++= allDeps)
  .settings(
    mainClass in Compile := Some("core.App"),
    mainClass in assembly := Some("core.App"),
    assemblyOutputPath in assembly := file("target/app.jar"),
    assemblyJarName in assembly := "app.jar",
    test in assembly := {},
    copyRes <<= (baseDirectory, target) map {
      (base, trg) =>
        val fromFile = new File(base, "../instructions.txt")
        val toFile = new File(base, "../target/instructions.txt")
        Files.copy(fromFile.toPath, toFile.toPath, StandardCopyOption.REPLACE_EXISTING)
    },
    assembly <<= assembly.dependsOn (copyRes),
    fork in run := true,
    javaOptions in run := Seq("-Xmx1G", "-Xss256k", "-XX:+UseCompressedOops")
  )


resolvers in Global ++= Seq(
  "Sbt plugins" at "https://dl.bintray.com/sbt/sbt-plugin-releases",
  "Maven Central Server" at "http://repo1.maven.org/maven2",
  "TypeSafe Repository Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "TypeSafe Repository Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/",
  Resolver.mavenLocal
)

cancelable in Global := true

