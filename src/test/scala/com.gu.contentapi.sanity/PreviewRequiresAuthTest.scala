package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class PreviewRequiresAuthTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "GETting preview content" should "require authentication" in {

    val httpRequest =  request(Config.previewHost).get
    teamCityNotifier("Getting Preview content without credentials should 401", "Getting Preview content without credentials did not result in the expected response code") {
      whenReady(httpRequest) { result =>
        result.status should be (401)
      }
    }
  }
}
