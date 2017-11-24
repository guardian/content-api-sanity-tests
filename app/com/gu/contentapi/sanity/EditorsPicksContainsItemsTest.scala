package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import com.gu.contentapi.sanity.tags.ProdOnly
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

@ProdOnly
class EditorsPicksContainsItemsTest(context: Context, wsClient: WSClient) extends SanityTestBase(context, wsClient) {

  "Editors picks for /uk" should "not be empty" in {
    val httpRequest = requestHost("/uk?show-editors-picks=true").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      ((json \ "response" \ "editorsPicks")(0) \ "id").asOpt[String] should not be(None)
    }
  }

}

