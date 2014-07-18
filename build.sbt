scalaVersion := "2.10.4"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies += "org.quartz-scheduler" % "quartz" % "2.1.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.4"

libraryDependencies += "com.typesafe.play" %% "play" % "2.2.2"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.41.0"

parallelExecution in ThisBuild := false
