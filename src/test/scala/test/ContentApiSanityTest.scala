package com.gu.contentapi.sanity

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.typesafe.config.ConfigFactory


class ContentApiSanityTest extends FlatSpec with ShouldMatchers with HttpHandler {

  val CacheControl = """public, max-age=(\d+)""".r

  "Content api" should "serve gzipped" in {


    val conf = ConfigFactory.load()
    conf.checkValid(ConfigFactory.defaultReference(), "content-api-sanity-tests")

    val connection = GET(
      conf.getString("content-api-sanity-tests.host")+"/search?format=json&api-key="+conf.getString("content-api-sanity-tests.api-key"),
      compress = true
    )

    connection.bodyFromGzip should include("results")
  }
}


