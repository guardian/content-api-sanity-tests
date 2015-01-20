package com.gu.contentapi.sanity

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{JsValue, Json}

class NewestItemFieldsTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The newest items" should "include mandatory fields" in {

    handleException {
      val mandatoryItemFields = List[String](
        "webTitle",
        "sectionName",
        "sectionId",
        "id",
        "webUrl",
        "apiUrl")
      val mandatoryTagFields = List[String](
        "id",
        "webTitle",
        "type",
        "sectionId",
        "sectionName",
        "webUrl",
        "apiUrl")

      val httpRequest = requestHost("search?order-by=newest&show-tags=all").get
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val newestItemList = (json \ "response" \ "results").as[List[Map[String, JsValue]]]
        for (item <- newestItemList) {
          val id = item.getOrElse("id", "ID for item was missing")

          for (mandatoryField <- mandatoryItemFields) {
            withClue( s"""Mandatory field not found! "$mandatoryField" for item: $id""") {
              item.contains(mandatoryField) should be(true)
            }
          }

          val tags = item.get("tags").get
          val firstTag = tags(0)
          val firstTagList = firstTag.as[Map[String, String]]
          for (mandatoryTagField <- mandatoryTagFields) {
            withClue( s"""Mandatory tag field not found! "$mandatoryTagField" for item: $id""") {
              firstTagList.contains(mandatoryTagField)
            }
          }
        }
      }
    }(fail, testNames.head, tags)
  }
}