import com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.Systemd
enablePlugins(JavaServerAppPackaging, SystemdPlugin)

name := "sanity-tests"

version := "1.0"

scalaVersion := "2.13.18"

scalacOptions ++= Seq("-feature", "-release:11")

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayNettyServer)

val AwsVersion = "2.55.11"
val JacksonVersion = "2.18.11"

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
  "at.yawk.lz4" % "lz4-java" % "1.11.4",
  "com.google.guava" % "guava" % "33.7.2-jre",
  "org.scala-lang.modules" %% "scala-xml" % "2.3.0",  //this version is wanted by scalatest, which has more use for it in this project than play.
  "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % JacksonVersion,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % JacksonVersion,
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % JacksonVersion,
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % JacksonVersion,
  "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % JacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion,
  "ch.qos.logback" % "logback-classic" % "1.5.34",
  "ch.qos.logback" % "logback-core" % "1.5.34",
)

testOptions ++= Seq("-u", "target/junit-test-reports").map(Tests.Argument(_))

excludeDependencies += ExclusionRule("org.lz4", "lz4-java")

// Sanity suites use WSClient, not browser helpers. Keep play-test itself.
excludeDependencies ++= Seq(
  ExclusionRule(organization = "io.fluentlenium"),
  ExclusionRule(organization = "org.seleniumhq.selenium"),
  ExclusionRule("org.scalatestplus", "selenium-4-12_2.13")
)

Universal / packageName := normalizedName.value
maintainer := "Guardian Content Platforms <content-platforms.dev@theguardian.com>"

Debian / serverLoading := Some(Systemd)
Debian / daemonUser := "content-api"
Debian / daemonGroup := "content-api"
