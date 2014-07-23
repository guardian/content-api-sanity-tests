package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class PreviewRequiresAuthTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "GETting preview content" should "require authentication" taggedAs(FrequentTest, PRODTest) in {

    handleException {
      val httpRequest = request(Config.previewHost).get
      whenReady(httpRequest) { result =>
        result.status should be(401)
      }
    }(fail,testNames.head, tags)
  }
}
