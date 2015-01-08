package com.gu.contentapi.sanity

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json

class NewestItemFieldsTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The newest item" should "include mandatory fields" taggedAs(FrequentTest, PRODTest)  in {

    handleException {
      val httpRequest = requestHost("search?order-by=newest&show-tags=all&show-fields=headline").get
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val newestItem = ((json \ "response" \ "results")(0))
      val newestItemId = (newestItem \ "id").asOpt[String]
      val mandatoryFields = List[String]("webTitle","webPublicationDate","sectionName","sectionId","id","webUrl","apiUrl")
      for(mandatoryField <- mandatoryFields)
        withClue (s"Mandatory field $mandatoryField for $newestItemId was not found \n") {
          (newestItem \ mandatoryField).asOpt[String] should be (defined)
          (newestItem \ mandatoryField).asOpt[String] should not be empty
        }
      val headline = (newestItem \ "fields" \ "headline").asOpt[String]
      withClue (s"Mandatory field headline for $newestItemId was not found \n") {
        headline should be (defined)
        headline should not be empty
      }

      val newestItemFirstTag = (newestItem \ "tags")(0)
      val tagMandatoryFields = List("id","webTitle","type","sectionId","sectionName","webUrl","apiUrl")
      for(tagMandatoryField <- tagMandatoryFields)
        withClue (s"Mandatory tag field $tagMandatoryField for $newestItemId was not found \n") {
          (newestItemFirstTag \ tagMandatoryField).asOpt[String] should be (defined)
          (newestItemFirstTag \ tagMandatoryField).asOpt[String] should not be empty
        }

    }
  }(fail,testNames.head, tags)
 }
}
