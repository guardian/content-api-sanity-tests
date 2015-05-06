package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.json.Json

class EditorsPicksContainsItemsTest(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

  "Editors picks for /uk" should "not be empty" in {
    val httpRequest = requestHost("/uk?show-editors-picks=true").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val mostViewedJson = (json \ "response" \ "editorsPicks")
      val mostViewedList = mostViewedJson.as[List[Map[String, String]]]
      mostViewedList should not be empty
    }
  }

}

