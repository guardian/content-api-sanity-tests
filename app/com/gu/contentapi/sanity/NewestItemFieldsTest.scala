package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.json.{JsValue, Json}

class NewestItemFieldsTest(context: Context) extends SanityTestBase(context) {

  "The newest items" should "include mandatory fields" in {
    val mandatoryItemFields = Seq[String](
      "webTitle",
      "sectionName",
      "sectionId",
      "id",
      "webUrl",
      "apiUrl")

    val httpRequest = requestHost("search?order-by=newest").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val newestItemList = (json \ "response" \ "results").asOpt[List[Map[String, JsValue]]]
      for (item <- newestItemList.value) {
        val id = item.getOrElse("id", "ID for item was missing")

        for (mandatoryField <- mandatoryItemFields) {
          withClue( s"""Mandatory field not found! "$mandatoryField" for item: $id""") {
            item.contains(mandatoryField) should be (true)
          }
        }
      }
    }
  }
}