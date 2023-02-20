name := "haveibeenpwned-akka-http"

scalaVersion := "2.13.10"

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.4.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "org.scalatest" %% "scalatest" % "3.2.15" % "test"
)
