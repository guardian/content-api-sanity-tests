scalaVersion := "2.10.4"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "theatr.us" at "http://repo.theatr.us"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.4" % "test"

libraryDependencies += "com.typesafe.play" %% "play" % "2.2.2"

libraryDependencies += "com.typesafe" % "config" % "1.2.0"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.41.0" % "test"

parallelExecution in ThisBuild := false

libraryDependencies += "us.theatr" %% "akka-quartz" % "0.2.0"
