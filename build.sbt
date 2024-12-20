import com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.Systemd
enablePlugins(JavaServerAppPackaging, SystemdPlugin)

name := "sanity-tests"

version := "1.0"

scalaVersion := "2.13.15"

scalacOptions ++= Seq("-feature", "-release:11")

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayNettyServer)

val AwsVersion = "2.27.24"

libraryDependencies ++= Seq(
  "org.quartz-scheduler" % "quartz" % "2.5.0",
  "org.scalatest" %% "scalatest" % "3.2.19",
  ws,
  "software.amazon.awssdk" % "s3" % AwsVersion,
  "software.amazon.awssdk" % "cloudwatch" % AwsVersion,
  "net.logstash.logback" % "logstash-logback-encoder" % "8.0",
  "joda-time" % "joda-time" % "2.13.0", //to fix "object joda is not a member.." error that appeared after dependency override akka-http-core_2.13
)

dependencyOverrides ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.3.0",  //this version is wanted by scalatest, which has more use for it in this project than play.
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.17.3",
  "ch.qos.logback" % "logback-classic" % "1.5.12",
  "ch.qos.logback" % "logback-core" % "1.5.12",
)

testOptions ++= Seq("-u", "target/junit-test-reports").map(Tests.Argument(_))

Universal / packageName := normalizedName.value
maintainer := "Guardian Content Platforms <content-platforms.dev@theguardian.com>"

Debian / serverLoading := Some(Systemd)
Debian / daemonUser := "content-api"
Debian / daemonGroup := "content-api"
