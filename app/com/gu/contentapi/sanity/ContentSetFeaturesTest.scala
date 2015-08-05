package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.json.{JsArray, JsValue, Json}

class ContentSetFeaturesTest(context: Context) extends SanityTestBase(context) {

  "GETting the preview content set JSON" should "show no results on live" in {
    val httpRequest = requestHost("search?content-set=preview").get()

    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val total = (json \ "response" \ "total").asOpt[Long]
      val results = Json.stringify(json \ "response" \ "results")
      total.value should equal(0)
      results should equal("[]")
    }
  }

  "GETting the print-sent content set JSON" should "return a newspaper-book-section tag for each result" in {
    val httpRequest = requestHost("search?content-set=print-sent&show-tags=newspaper-book-section").get()

    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val results = (json \ "response" \ "results").asOpt[List[Map[String, JsValue]]]
      for (result <- results.value) {
        val id = result.getOrElse("id", "ID was missing")
        val tags = result.get("tags")
        withClue(s"tags was not found or empty! for item: $id") {
          tags should be (defined)
          tags.get.asInstanceOf[JsArray].value.length should be (1)
        }
      }
    }
  }

}
