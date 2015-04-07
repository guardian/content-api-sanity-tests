package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.json.Json

class PreviewContentSetNotInLiveTest(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

  "GETting the preview content set JSON" should "show no results on live" in {
    val httpRequest = requestHost("search?content-set=preview").get()

    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val total = (json \ "response" \ "total").asOpt[Long]
      val results = Json.stringify(json \ "response" \ "results")
      total.value should equal(0)
      results should equal("[]")
    }
  }



}
