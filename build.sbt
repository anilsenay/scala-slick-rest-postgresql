ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val akkaVersion = "2.6.19"
val akkaHttpVersion = "10.2.9"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-stream"           % akkaVersion,
  "com.typesafe.akka"          %% "akka-actor-typed"     % akkaVersion,
  // akka http
  "com.typesafe.akka"          %% "akka-http"             % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-http-spray-json"  % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-http-testkit"     % akkaHttpVersion,
  "org.postgresql"             %  "postgresql"            % "42.3.6",
  "org.slf4j"                  %  "slf4j-nop"             % "1.7.36",
  "com.typesafe.slick"         %% "slick-hikaricp"        % "3.3.3",
  "com.typesafe.scala-logging" %% "scala-logging"         % "3.9.5",
  "ch.qos.logback"             % "logback-classic"        % "1.2.11",
)

lazy val root = (project in file("."))
  .settings(
    name := "Scala-REST"
  )
