package com.gu.contentapi.sanity

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}

class KindleTagTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Tags required by the Kindle app" should "exist" in {
    handleException{

      val kindleTags = Seq(
        "theguardian/mainsection",
        "theguardian/mainsection/topstories",
        "tone/advertisement-features",
        "uk/uk",
        "theobserver/news/worldnews")

      for (kindleTag <- kindleTags) {
        val httpRequest = requestHost(kindleTag).get
        whenReady(httpRequest) { result =>
          assume(result.status != 503, "Service is down")
          withClue("Failed attempting to GET " + Config.host + kindleTag) {
            result.status should equal(200)
          }
          withClue("Response for " + Config.host + kindleTag + "did not contain 'tag'") {
            result.body should include("tag")
          }
        }
      }
    }(fail, testNames.head, tags)
  }
}
