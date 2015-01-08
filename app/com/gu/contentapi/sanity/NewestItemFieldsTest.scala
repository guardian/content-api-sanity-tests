package com.gu.contentapi.sanity

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{JsValue, Json}

class NewestItemFieldsTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The newest item" should "include mandatory fields" taggedAs(FrequentTest, PRODTest)  in {

    handleException {
      val httpRequest = requestHost("search?order-by=newest&show-tags=all&show-fields=headline").get
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val newestItem = ((json \ "response" \ "results")(0))
      val newestItemFirstTag = (newestItem \ "tags")(0)
      val newestItemId = (newestItem \ "id").asOpt[String].getOrElse("ID not found")
      val mandatoryFields = List[JsValue] (newestItem\"webTitle",newestItem\"sectionName",newestItem\"sectionId",newestItem\"id",newestItem\"webUrl",newestItem\"id",newestItem\"webUrl",newestItem\"apiUrl",newestItemFirstTag\"id",newestItemFirstTag\"webTitle",newestItemFirstTag\"type",newestItemFirstTag\"sectionId",newestItemFirstTag\"sectionName",newestItemFirstTag\"webUrl",newestItemFirstTag\"apiUrl",newestItem\"fields"\"headline")
      for(mandatoryField <- mandatoryFields)
        withClue (s"Mandatory field not found! $mandatoryField for ID: $newestItemId") {
          (mandatoryField).asOpt[String] should be (defined)
          (mandatoryField).asOpt[String] should not be empty
        }
    }
  }(fail,testNames.head, tags)
 }
}
