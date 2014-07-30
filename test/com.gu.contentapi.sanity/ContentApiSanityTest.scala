package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class ContentApiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Content api" should "serve gzipped" taggedAs(FrequentTest) in {
    // GZip Compression is enabled in application.conf

    val httpRequest = requestHost("search?format=json&api-key=").get
    whenReady(httpRequest) { result =>
      withClue("Status code was: " + result.status + ", result body was: " + result.body) {
        result.body should include("results")
      }
    }
  }
}

