package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.tags.ProdOnly
import play.api.libs.json.Json

@ProdOnly
class MostViewedContainsItemsTest extends SanityTestBase {

  "Most Viewed" should "contain more than 10 items" in {

    handleException {
      val httpRequest = requestHost("/uk?show-most-viewed=true").get
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val mostViewedJson = (json \ "response" \ "mostViewed")
        val mostViewedList = mostViewedJson.as[List[Map[String, String]]]
        mostViewedList.length should be > 10
      }
    }(fail,testNames.head, tags)
  }

}

