package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.json.Json

class ValidateArticleSchema(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

  "The Content API" should "return correct article schema" in {
    val httpRequest = requestHost("/lifeandstyle/lostinshowbiz/2014/sep/18/charlotte-crosby-blueprint-for-civilisation-geordie-shore-celebrity-big-brother?show-fields=all&show-elements=all").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val sectionName = (json \ "response" \ "content" \ "sectionName").asOpt[String]
      sectionName.value should be ("Life and style")
      val headline = (json \ "response" \ "content" \ "fields" \ "headline").asOpt[String]
      headline.value should be ("Charlotte Crosby: a blueprint for civilisation")
      val mediaId = Option((json \ "response" \ "content" \ "elements")(0) \\ "mediaId")
      mediaId.value shouldBe a [List[_]]
    }
  }

}
