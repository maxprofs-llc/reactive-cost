name := "reactive-cost"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.0",
  "org.webjars" % "angularjs" % "1.2.13",
  "org.mongodb" %% "casbah" % "2.5.0",
  "com.typesafe.akka" %% "akka-actor" % "2.2.0",
  "org.specs2" %% "specs2" % "1.14" % "test",
  "commons-codec" % "commons-codec" % "1.7",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.0",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "com.google.guava" % "guava" % "16.0",
  "com.google.code.findbugs" % "jsr305" % "2.0.0",
  "org.reactivemongo" %% "reactivemongo" % "0.10.0"
)     

play.Project.playScalaSettings
