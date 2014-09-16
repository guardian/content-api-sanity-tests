package com.gu.contentapi.sanity

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json

class SearchContainsLargeNumberOfResults extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The Content API" should "return a large total of results on the /search endpoint" taggedAs(FrequentTest, PRODTest)  in {

    handleException {
      val httpRequest = requestHost("search").get
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val resultTotal = (json \ "response" \ "total").as[Int]
        resultTotal should be >= 1819326
      }
    }(fail,testNames.head, tags)
  }

}
