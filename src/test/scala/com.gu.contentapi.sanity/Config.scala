package com.gu.contentapi.sanity

import com.typesafe.config.ConfigFactory

object Config {
  val conf = ConfigFactory.load()
  val sanityConfig = conf.getConfig("content-api-sanity-tests")
  conf.checkValid(ConfigFactory.defaultReference(), "content-api-sanity-tests")

  val host = sanityConfig.getString("host")
  val apiKey = sanityConfig.getString("api-key")
  val writeHost = sanityConfig.getString("write-host")
  val writeUsername = sanityConfig.getString("write-username")
  val writePassword = sanityConfig.getString("write-password")
}
