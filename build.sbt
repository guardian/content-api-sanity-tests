name := "content-api-sanity-tests"

version := "1.0-SNAPSHOT"

libraryDependencies += "org.quartz-scheduler" % "quartz" % "2.1.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.4"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.41.0"

parallelExecution in ThisBuild := false

play.Project.playScalaSettings

javaOptions ++= collection.JavaConversions.propertiesAsScalaMap(System.getProperties).map{ case (key,value) => "-D" + key + "=" +value }.toSeq