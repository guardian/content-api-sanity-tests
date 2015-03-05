package com.gu.contentapi.sanity

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}

class CriticalTagsTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Tags critical for publication (including Kindle)" should "exist" in {
    handleException{

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
        val httpRequest = requestHost(criticalTag).get
        whenReady(httpRequest) { result =>
          assume(result.status != 503, "Service is down")
          withClue("Failed attempting to GET " + Config.host + criticalTag) {
            result.status should equal(200)
          }
          withClue("Response for " + Config.host + criticalTag + "did not contain 'tag'") {
            result.body should include("tag")
          }
        }
      }
    }(fail, testNames.head, tags)
  }
}
