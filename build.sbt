ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"


libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "dynamodb" % "2.20.25",  // AWS SDK V2 DynamoDB
  "io.circe" %% "circe-core" % "0.14.2",             // Circe Core
  "io.circe" %% "circe-generic" % "0.14.2",           // Circe Generic for case class derivation
  "io.circe" %% "circe-parser" % "0.14.2",            // Circe Parser
  "org.typelevel" %% "cats-effect" % "3.3.5",         // Cats Effect
)

libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.3.18"
libraryDependencies += "software.amazon.awssdk" % "sts" % "2.20.56"




lazy val root = (project in file("."))
  .settings(
    name := "HelperRepo"
  )
