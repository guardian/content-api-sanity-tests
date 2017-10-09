name := "sanity-tests"

version := "1.0"

scalaVersion := "2.11.11"

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-target:jvm-1.8", "-Xfatal-warnings")

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayAkkaHttpServer)
  .disablePlugins(PlayNettyServer)

val AwsVersion = "1.11.193"

libraryDependencies ++= Seq(
  "org.quartz-scheduler" % "quartz" % "2.3.0",
  "org.scalatest" %% "scalatest" % "3.0.4",
  "org.seleniumhq.selenium" % "selenium-java" % "3.5.3",
  "joda-time" % "joda-time" % "2.9.9",
  ws,
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.1",
  "com.typesafe.play" %% "play-json" % "2.6.6",
  "com.amazonaws" % "aws-java-sdk-cloudwatch" % AwsVersion,
  "com.amazonaws" % "aws-java-sdk-s3" % AwsVersion
)

routesGenerator := InjectedRoutesGenerator

testOptions ++= Seq("-u", "target/junit-test-reports").map(Tests.Argument(_))
