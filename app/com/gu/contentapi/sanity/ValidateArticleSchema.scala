package com.gu.contentapi.sanity

import play.api.libs.json.Json

class ValidateArticleSchema extends SanityTestBase {

  "The Content API" should "return correct article schema" in {

    handleException {
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
    }(fail,testNames.head, tags)
  }

}
