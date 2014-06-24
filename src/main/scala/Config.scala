package com.gu.contentapi

import com.typesafe.config.ConfigFactory

object Config {
  val conf = ConfigFactory.load()
  val sanityConfig = conf.getConfig("content-api-sanity-tests")
  conf.checkValid(ConfigFactory.defaultReference(), "content-api-sanity-tests")
  val pagerDutyServiceKey=sanityConfig.getString("pager-duty-service-key")
}

