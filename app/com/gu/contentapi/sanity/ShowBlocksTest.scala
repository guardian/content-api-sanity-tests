package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.json.{JsArray, JsValue, Json}

class ShowBlocksTest(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

  "The newest Flex items in the search results" should "include blocks if show-blocks=all" in {

    val httpRequest = requestHost("search?show-blocks=all&show-fields=internalComposerCode,body").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      val json = Json.parse(result.body)
      val newestItemList = (json \ "response" \ "results").asOpt[List[Map[String, JsValue]]]
      for (item <- newestItemList.value) {
        if (hasInternalComposerCode(item)) {
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
      val newestFlexItem = newestItemList.find(hasInternalComposerCode)
      newestFlexItem foreach { item =>
        // Second request to the item endpoint for the first flexible content item in the search results
        val itemPath = item("id").as[String]
        val itemRequest = requestHost(s"$itemPath?show-blocks=all&show-fields=body").get()
        whenReady(itemRequest) { result =>
          assume(result.status == 200, "Service is down")
          val json = Json.parse(result.body)
          val item = (json \ "response" \ "content").asOpt[Map[String, JsValue]]
          checkBlocks(item.value)
        }
      }
    }
  }

  def hasInternalComposerCode(item: Map[String, JsValue]): Boolean =
    item.get("fields").flatMap(fields => (fields \ "internalComposerCode").asOpt[String]).isDefined

  def hasNonEmptyBody(item: Map[String, JsValue]): Boolean =
    item.get("fields").flatMap(fields => (fields \ "body").asOpt[String]).exists(_.trim.nonEmpty)

  def checkBlocks(item: Map[String, JsValue]) = {
    val id = item.getOrElse("id", "ID for item was missing")

    withClue(s"blocks field not found! for item: $id") {
      item should contain key "blocks"
    }

    val blocks = item("blocks")

    // Many articles in CODE don't have a main block
    // (because people don't bother adding a main image when manually testing Composer)
    // so don't check for the presence of a main block.

    val bodyBlocks = (blocks \ "body").asOpt[JsArray]
    withClue(s"blocks.body field not found! for item: $id") {
      bodyBlocks should be (defined)
    }

    // Some content (e.g. cartoons) have an empty body and no body blocks,
    // so only check body blocks if the body exists and is non-empty.
    if (hasNonEmptyBody(item)) {
      withClue(s"blocks.body is an empty list! for item: $id") {
        bodyBlocks.get.value should not be empty
      }
      withClue(s"first body block has no bodyHtml! for item: $id") {
        (bodyBlocks.get.apply(0) \ "bodyHtml").as[String] should not be empty
      }
    }

  }

}
