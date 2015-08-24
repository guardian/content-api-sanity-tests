package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import org.scalatest.tagobjects.Retryable
import org.scalatest.time.{Seconds, Span}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

/**
 * Performs a (nearly) end-to-end test by:
 * 1. POSTing to a special endpoint on Porter that creates a new piece of known content and pushes it to Attendant's queue
 * 2. Checking that the content shows up in Concierge with a recent lastModified timestamp
 */
class CanaryContentSanityTest(context: Context) extends SanityTestBase(context) {

  private def retrieveCanaryLastModifiedTimestamp(): Option[DateTime] = {
    val httpRequest = requestHost("/canary?show-fields=lastModified").get()
    whenReady(httpRequest) { result =>
      val stringValue = (result.json \ "response" \ "content" \ "fields" \ "lastModified").asOpt[String]
      stringValue.map(new DateTime(_))
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
      .withHeaders("Content-Type" -> "application/json")
      .post("")
    whenReady(httpRequest) { result =>
      withClue("Response code was " + result.status + " expected " + postSuccessResponseCode) {
        result.status should equal(postSuccessResponseCode)
      }

       result.status match {
          case 202 =>
            
            val thirtySecondsAgo = DateTime.now.minusSeconds(30)
            eventually(timeout(Span(30, Seconds))) {
              val lastModified = retrieveCanaryLastModifiedTimestamp()
              withClue(s"Canary content did not show a lastModified >= $thirtySecondsAgo. lastModified field was $lastModified") {
                lastModified.value.isAfter(thirtySecondsAgo) should be(true)
              }
            }

          case 555 => /*  push to elasticsearch switch is not activated on porter - can't test */
          case _   => fail("Response code was " + result.status + " expected " + postSuccessResponseCode)
        }
    }
  }
}
