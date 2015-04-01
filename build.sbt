import play.PlayScala

name := "sanity-tests"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "org.quartz-scheduler" % "quartz" % "2.1.6",
  "org.scalatest" %% "scalatest" % "2.1.4",
  "org.seleniumhq.selenium" % "selenium-java" % "2.41.0",
  "joda-time" % "joda-time" % "2.7",
  ws,
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3",
  "com.github.cb372" %% "play-configurable-ningwsplugin" % "0.2",
  "org.scalatestplus" %% "play" % "1.1.1"
)

parallelExecution in ThisBuild := false

javaOptions ++= collection.JavaConversions.propertiesAsScalaMap(System.getProperties).map{ case (key,value) => "-D" + key + "=" +value }.toSeq

testOptions ++= Seq("-u", "target/test-reports").map(Tests.Argument(_))
