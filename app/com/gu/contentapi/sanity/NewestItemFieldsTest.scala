package com.gu.contentapi.sanity

import play.api.libs.json.{JsValue, Json}

class NewestItemFieldsTest extends SanityTestBase {

  "The newest items" should "include mandatory fields" in {

    handleException {
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
    }(fail, testNames.head, tags)
  }
}