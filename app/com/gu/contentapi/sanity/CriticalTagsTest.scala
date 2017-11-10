package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.ws.WSClient

class CriticalTagsTest(context: Context, wsClient: WSClient) extends SanityTestBase(context, wsClient) {

  "Tags critical for publication (including Kindle)" should "exist" in {
    val criticalTags = Seq(
      "theguardian/mainsection",
      "theguardian/mainsection/topstories",
      "tone/advertisement-features",
      "uk/uk",
      "theobserver/news/worldnews",
      "business/business",
      "culture/culture",
      "football/football",
      "theguardian/mainsection/opinion",
      "lifeandstyle/lifeandstyle",
      "environment/environment"
    )

    for (criticalTag <- criticalTags) {
      val httpRequest = requestHost(criticalTag).get()
      whenReady(httpRequest) { result =>
        assumeNot(503, 504)(result)
        withClue("Failed attempting to GET " + Config.host + criticalTag) {
          result.status should equal(200)
        }
        withClue("Response for " + Config.host + criticalTag + "did not contain 'tag'") {
          result.body should include("tag")
        }
      }
    }
  }
}
