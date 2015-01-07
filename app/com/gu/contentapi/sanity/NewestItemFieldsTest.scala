package com.gu.contentapi.sanity

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json

class NewestItemFieldsTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The newest item" should "include mandatory fields" taggedAs(FrequentTest, PRODTest)  in {

    handleException {
      val httpRequest = requestHost("search?order-by=newest").get
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val newestItem = (json \ "response" \ "results")(0)
        val mandatoryFields = List("webTitle","webPublicationDate","sectionName","sectionId","id","webUrl","apiUrl")
        for(mandatoryField <- mandatoryFields)
        withClue (s"Mandatory field $mandatoryField was not found \n") {
          (newestItem \ mandatoryField).asOpt[String] should be (defined)
          (newestItem \ mandatoryField).asOpt[String] should not be empty
        }
      }
    }(fail,testNames.head, tags)
  }


}
