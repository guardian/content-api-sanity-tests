// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.10")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.4")

addDependencyTreePlugin

// sbt-web brings in Jackson separately from the application's dependency graph.
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.18.11"
