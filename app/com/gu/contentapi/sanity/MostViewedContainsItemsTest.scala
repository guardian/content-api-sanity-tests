package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import com.gu.contentapi.sanity.tags.ProdOnly
import play.api.libs.json.Json

@ProdOnly
class MostViewedContainsItemsTest(context: Context) extends SanityTestBase(context) {

  "Most Viewed" should "contain more than 10 items" in {
    val httpRequest = requestHost("/uk?show-most-viewed=true").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      // most viewed length should be > 10 so try and access 11th element in list.
      (json \ "response" \ "mostViewed")(10).asOpt[String] should not be(None)
    }
  }

}

