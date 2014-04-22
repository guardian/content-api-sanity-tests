package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec, matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class ContentApiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Content api" should "serve gzipped" in {
    // GZip Compression is enabled in application.conf

    val httpRequest = request(Config.host + "search?format=json&api-key=" + Config.apiKey).get()
    whenReady(httpRequest) { result =>
      result.body should include ("results")
    }
  }
}


