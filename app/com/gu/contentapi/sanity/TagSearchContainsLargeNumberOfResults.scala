package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.json.Json

class TagSearchContainsLargeNumberOfResults(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

  "The Content API" should "return a large total of tags on the /tags endpoint" in {
    val httpRequest = requestHost("tags").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val resultTotal = (json \ "response" \ "total").asOpt[Int]
      resultTotal.value should be >= 40000
    }
  }

}
