package com.gu.contentapi.sanity

import com.typesafe.config.ConfigFactory

object Config {
  val conf = ConfigFactory.load()
  val sanityConfig = conf.getConfig("content-api-sanity-tests")
  conf.checkValid(ConfigFactory.defaultReference(), "content-api-sanity-tests")

  val host = sanityConfig.getString("host")
  val hostPublicSecure = sanityConfig.getString("host-public-secure")
  val r2AdminHost = sanityConfig.getString("r2-admin-host")
  val r2AdminUsername = sanityConfig.getString("r2-admin-username")
  val r2AdminPassword = sanityConfig.getString("r2-admin-password")
  val previewHost = sanityConfig.getString("preview-host")
  val previewUsernameCode = sanityConfig.getString("preview-username")
  val previewPasswordCode = sanityConfig.getString("preview-password")
  val apiKey = sanityConfig.getString("api-key")
  val writeHost = sanityConfig.getString("write-host")
  val writePreviewHost = sanityConfig.getString("write-preview-host")
  val pagerDutyServiceKey=sanityConfig.getString("pager-duty-service-key")
  val pagerDutyServiceKeyLowPriority=sanityConfig.getString("pager-duty-service-key-low-priority")
  val composerHost = sanityConfig.getString("composer-host")
  val apiIndexerLiveHost = sanityConfig.getString("api-indexer-live-host")
  val apiIndexerPreviewHost = sanityConfig.getString("api-indexer-preview-host")
}
