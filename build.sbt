scalaVersion := "2.10.0"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.3" % "test"

libraryDependencies += "org.scalaj" % "scalaj-http_2.10" % "0.3.14"

libraryDependencies += "com.typesafe.play" % "play_2.10" % "2.2.2"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"

libraryDependencies += "com.typesafe" % "config" % "1.2.0"