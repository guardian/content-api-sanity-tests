package com.gu.contentapi.sanity

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json
import org.scalatest.OptionValues._

class TagSearchContainsLargeNumberOfResults extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The Content API" should "return a large total of tags on the /tags endpoint" in {

    handleException {
      val httpRequest = requestHost("tags").get
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val resultTotal = (json \ "response" \ "total").asOpt[Int]
        resultTotal.value should be >= 40000
      }
    }(fail,testNames.head, tags)
  }

}
