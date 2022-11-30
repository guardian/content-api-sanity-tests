package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import org.scalatest.tagobjects.Retryable
import org.scalatest.time.{Seconds, Span}
import play.api.libs.ws.WSClient

import java.time.LocalDateTime

/**
 * Performs a (nearly) end-to-end test by:
 * 1. POSTing to a special endpoint on Porter that creates a new piece of known content and pushes it to Attendant's queue
 * 2. Checking that the content shows up in Concierge with a recent lastModified timestamp
 */
class CanaryContentSanityTest(context: Context, wsClient: WSClient) extends SanityTestBase(context, wsClient) {

  private def retrieveCanaryLastModifiedTimestamp(): Option[LocalDateTime] = {
    val httpRequest = requestHost("/canary?show-fields=lastModified").get()
    whenReady(httpRequest) { result =>
      val stringValue = (result.json \ "response" \ "content" \ "fields" \ "lastModified").asOpt[String]
      stringValue.map(LocalDateTime.parse)
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

      val thirtySecondsAgo = LocalDateTime.now.minusSeconds(30)
      eventually(timeout(Span(30, Seconds))) {
        val lastModified = retrieveCanaryLastModifiedTimestamp()
        withClue(s"Canary content did not show a lastModified >= $thirtySecondsAgo. lastModified field was $lastModified") {
          lastModified.value.isAfter(thirtySecondsAgo) should be(true)
        }
      }
    }
  }
}
