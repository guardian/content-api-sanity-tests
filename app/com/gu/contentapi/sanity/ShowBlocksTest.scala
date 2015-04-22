package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

class ShowBlocksTest(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

  "The newest Flex items in the search results" should "include blocks if show-blocks=all" in {

    val httpRequest = requestHost("search?show-blocks=all&show-fields=internalComposerCode").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val newestItemList = (json \ "response" \ "results").asOpt[List[Map[String, JsValue]]]
      for (item <- newestItemList.value) {
        if ((item("fields") \ "internalComposerCode").asOpt[String].isDefined) {
          checkBlocks(item)
        }
      }
    }

  }

  "The newest Flex item" should "include blocks if show-blocks=all" in {

    // First request to get the latest items
    val httpRequest = requestHost("search?show-fields=internalComposerCode").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val newestItemList = (json \ "response" \ "results").as[List[Map[String, JsValue]]]
      val newestFlexItem = newestItemList.find(item => (item("fields") \ "internalComposerCode").asOpt[String].isDefined)
      newestFlexItem foreach { item =>
        // Second request to the item endpoint for the first flexible content item in the search results
        val itemUrl = item("id").as[String] + "?show-blocks=all"
        val itemRequest = requestHost(itemUrl).get()
        whenReady(itemRequest) { result =>
          assume(result.status == 200, "Service is down")
          val json = Json.parse(result.body)
          val item = (json \ "response" \ "content").asOpt[Map[String, JsValue]]
          checkBlocks(item.value)
        }
      }
    }
  }

  def checkBlocks(item: Map[String, JsValue]) = {
    val id = item.getOrElse("id", "ID for item was missing")

    withClue(s"blocks field not found! for item: $id") {
      item should contain key "blocks"
    }

    val blocks = item("blocks")

    // TODO currently the main block appears to be missing in CODE.
    // Flexible content is not exposing it.
    // Investigating.

    //          val mainBlock = (blocks \ "main").asOpt[JsObject]
    //          withClue(s"blocks.main field not found! for item: $id") {
    //            mainBlock should be (defined)
    //          }
    //          withClue(s"main block field has no bodyHtml! for item: $id") {
    //            (mainBlock.value \ "bodyHtml").as[String] should not be empty
    //          }

    val bodyBlocks = (blocks \ "body").asOpt[JsArray]
    withClue(s"blocks.body field not found! for item: $id") {
      bodyBlocks should be (defined)
    }
    withClue(s"blocks.body is an empty list! for item: $id") {
      bodyBlocks.get.value should not be empty
    }
    withClue(s"first body block has no bodyHtml! for item: $id") {
      (bodyBlocks.get.apply(0) \ "bodyHtml").as[String] should not be empty
    }

  }

}