import com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.Systemd
enablePlugins(JavaServerAppPackaging, SystemdPlugin)

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
  ws,
  "com.amazonaws" % "aws-java-sdk-cloudwatch" % AwsVersion,
  "com.amazonaws" % "aws-java-sdk-s3" % AwsVersion
)

dependencyOverrides ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.1.0"  //this version is wanted by scalatest, which has more use for it in this project than play.
)

javaOptions ++= collection.JavaConversions.propertiesAsScalaMap(System.getProperties).map{ case (key,value) => "-D" + key + "=" +value }.toSeq

testOptions ++= Seq("-u", "target/junit-test-reports").map(Tests.Argument(_))

Universal / packageName := normalizedName.value
maintainer := "Guardian Content Platforms <content-platforms.dev@theguardian.com>"

Debian / serverLoading := Some(Systemd)
Debian / daemonUser := "content-api"
Debian / daemonGroup := "content-api"
