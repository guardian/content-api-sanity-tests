name := "sanity-tests"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature")

routesGenerator := StaticRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "org.quartz-scheduler" % "quartz" % "2.1.6",
  "org.scalatest" %% "scalatest" % "2.2.6",
  "org.seleniumhq.selenium" % "selenium-java" % "2.41.0",
  "joda-time" % "joda-time" % "2.7",
  ws,
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1",
  "com.amazonaws" % "aws-java-sdk-cloudwatch" % "1.10.2",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.97"
)

parallelExecution in ThisBuild := false

javaOptions ++= collection.JavaConversions.propertiesAsScalaMap(System.getProperties).map{ case (key,value) => "-D" + key + "=" +value }.toSeq

testOptions ++= Seq("-u", "target/junit-test-reports").map(Tests.Argument(_))
