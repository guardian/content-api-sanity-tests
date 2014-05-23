package com.gu.contentapi.sanity

import com.typesafe.config.ConfigFactory

object Config {
  val conf = ConfigFactory.load()
  val sanityConfig = conf.getConfig("content-api-sanity-tests")
  conf.checkValid(ConfigFactory.defaultReference(), "content-api-sanity-tests")
  val sanityCodeConfig = conf.getConfig("content-api-sanity-code")
  conf.checkValid(ConfigFactory.defaultReference(), "content-api-sanity-code")

  val host = sanityConfig.getString("host")
  val apiKey = sanityConfig.getString("api-key")
  val previewHost = sanityConfig.getString("preview-host")
  val writeHost = sanityConfig.getString("write-host")
  val writeUsername = sanityConfig.getString("write-username")
  val writePassword = sanityConfig.getString("write-password")
  val previewCodeHost = sanityCodeConfig.getString("preview-host")
  val composerHost = sanityCodeConfig.getString("composer-host")
}
