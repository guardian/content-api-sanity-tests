package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class ContentApiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Content api" should "serve gzipped" taggedAs(FrequentTest) in {
    handleException{
    // GZip Compression is enabled in application.conf

    val httpRequest = requestHost("search?format=json&api-key=").get
    whenReady(httpRequest) { result =>
      result.body should include("results")
      }
    }(fail, testNames.mkString)
  }
}


