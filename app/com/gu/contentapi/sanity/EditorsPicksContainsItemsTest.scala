package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import com.gu.contentapi.sanity.tags.ProdOnly
import play.api.libs.json.Json

@ProdOnly
class EditorsPicksContainsItemsTest(context: Context) extends SanityTestBase(context) {

  "Editors picks for /uk" should "not be empty" in {
    val httpRequest = requestHost("/uk?show-editors-picks=true").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val editorsPicksJson = (json \ "response" \ "editorsPicks")
      val editorsPicksList = editorsPicksJson.as[List[Map[String, String]]]
      editorsPicksList should not be empty
    }
  }

}

