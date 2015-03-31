package com.gu.contentapi.sanity

import play.api.libs.json.Json

class SearchContainsLargeNumberOfResults extends SanityTestBase {

  "The Content API" should "return a large total of results on the /search endpoint" in {

    handleException {
      val httpRequest = requestHost("search").get()
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val resultTotal = (json \ "response" \ "total").asOpt[Int]
        resultTotal.value should be >= 1819326
      }
    }(fail,testNames.head, tags)
  }

}
