name := "sanity-tests"

version := "1.0"

libraryDependencies += "org.quartz-scheduler" % "quartz" % "2.1.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.4"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.41.0"

parallelExecution in ThisBuild := false

play.Project.playScalaSettings
