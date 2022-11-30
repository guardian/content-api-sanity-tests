name := "sanity-tests"

version := "1.0"

scalaVersion := "2.13.10"

scalacOptions ++= Seq("-feature")

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayAkkaHttpServer)
  .disablePlugins(PlayNettyServer)

val AwsVersion = "1.11.227"

libraryDependencies ++= Seq(
  "org.quartz-scheduler" % "quartz" % "2.3.2",
  "org.scalatest" %% "scalatest" % "3.2.14",
  "org.seleniumhq.selenium" % "selenium-java" % "3.5.3",
  "joda-time" % "joda-time" % "2.9.9",
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0",
  "com.amazonaws" % "aws-java-sdk-cloudwatch" % AwsVersion,
  "com.amazonaws" % "aws-java-sdk-s3" % AwsVersion
)

javaOptions ++= collection.JavaConversions.propertiesAsScalaMap(System.getProperties).map{ case (key,value) => "-D" + key + "=" +value }.toSeq

testOptions ++= Seq("-u", "target/junit-test-reports").map(Tests.Argument(_))
