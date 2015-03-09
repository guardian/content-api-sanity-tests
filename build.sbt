import play.PlayScala

name := "sanity-tests"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += "org.quartz-scheduler" % "quartz" % "2.1.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.4"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.41.0"

libraryDependencies += "joda-time" % "joda-time" % "2.7"

libraryDependencies += ws

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3"

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3"

parallelExecution in ThisBuild := false

javaOptions ++= collection.JavaConversions.propertiesAsScalaMap(System.getProperties).map{ case (key,value) => "-D" + key + "=" +value }.toSeq
