name := "sanity-tests"

version := "1.0"

scalaVersion := "2.11.11"

scalacOptions ++= Seq("-feature")

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayAkkaHttpServer)
  .disablePlugins(PlayNettyServer)

val AwsVersion = "1.11.227"

libraryDependencies ++= Seq(
  "org.quartz-scheduler" % "quartz" % "2.3.0",
  "org.scalatest" %% "scalatest" % "3.0.4",
  "org.seleniumhq.selenium" % "selenium-java" % "3.5.3",
  "joda-time" % "joda-time" % "2.9.9",
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.1",
  "com.amazonaws" % "aws-java-sdk-cloudwatch" % AwsVersion,
  "com.amazonaws" % "aws-java-sdk-s3" % AwsVersion
)

javaOptions ++= collection.JavaConversions.propertiesAsScalaMap(System.getProperties).map{ case (key,value) => "-D" + key + "=" +value }.toSeq

testOptions ++= Seq("-u", "target/junit-test-reports").map(Tests.Argument(_))
