package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import org.scalatest.tagobjects.Retryable
import org.scalatest.time.{Seconds, Span}
import play.api.libs.ws.WSClient

import java.time.ZonedDateTime

/**
 * Performs a (nearly) end-to-end test by:
 * 1. POSTing to a special endpoint on Porter that creates a new piece of known content and pushes it to Attendant's queue
 * 2. Checking that the content shows up in Concierge with a recent lastModified timestamp
 */
class CanaryContentSanityTest(context: Context, wsClient: WSClient) extends SanityTestBase(context, wsClient) {

  private def retrieveCanaryLastModifiedTimestamp(): Option[ZonedDateTime] = {
    // since channels became possible - canary content is now hidden from the world by default and
    // only accessible from the /channel/canary path :)
    val httpRequest = requestHost("/channel/canary?show-fields=lastModified").get()
    whenReady(httpRequest) { result =>
      val stringValue = (result.json \ "response" \ "results" \ 0 \ "fields" \ "lastModified").asOpt[String]
      stringValue.map(ZonedDateTime.parse)
    }
  }

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetryOnFailure(super.withFixture(test))
    else
      super.withFixture(test)
  }

  "Touching the canary content" should "update the timestamp" taggedAs Retryable in {
    val postSuccessResponseCode = 202
    val httpRequest = request(Config.writeHost + "canary/content")
      .withHttpHeaders("Content-Type" -> "application/json")
      .post("")
    
    whenReady(httpRequest) { result =>
      assumeNot(504)(result)
      withClue("Response code was " + result.status + " expected " + postSuccessResponseCode) {
        result.status should equal(postSuccessResponseCode)
      }

      val thirtySecondsAgo = ZonedDateTime.now.minusSeconds(30)
      eventually(timeout(Span(30, Seconds))) {
        val lastModified = retrieveCanaryLastModifiedTimestamp()
        withClue(s"Canary content did not show a lastModified >= $thirtySecondsAgo. lastModified field was $lastModified") {
          lastModified.value.isAfter(thirtySecondsAgo) should be(true)
        }
      }
    }
  }
}
