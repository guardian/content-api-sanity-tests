package com.gu.contentapi.sanity

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json
import org.scalatest.OptionValues._


class PreviewContentSetNotInLiveTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "GETting the preview content set JSON" should "show no results on live" in {
    handleException{
      val httpRequest = requestHost("search?content-set=preview").get

      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val total = (json \ "response" \ "total").asOpt[Long]
        val results = Json.stringify(json \ "response" \ "results")
        total.value should equal(0)
        results should equal("[]")
      }
    }(fail, testNames.head, tags)
  }



}
