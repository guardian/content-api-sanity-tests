package com.gu.contentapi.sanity

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json


class PreviewContentSetNotInLiveTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "GETting the preview content set JSON" should "show no results on live" in {
    val httpRequest = request(Config.host + "search?content-set=preview").get()
    whenReady(httpRequest) { result =>
      val json = Json.parse(result.body)
      val total = (json \ "response" \ "total").as[Long]
      val results = Json.stringify(json \ "response" \ "results")
      total should equal (0)
      results should equal ("[]")
    }
  }



}
