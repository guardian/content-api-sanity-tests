package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import org.scalatest.tagobjects.Retryable
import org.scalatest.time.{Minutes, Seconds, Span}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import scala.io.Source
import org.scalatest.exceptions.TestFailedException
import play.api.libs.ws.{EmptyBody, WSAuthScheme}

class CanaryWritingSanityTest(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

  val now = new DateTime()
  val capiDateStamp = now.toString(ISODateTimeFormat.dateTimeNoMillis().withZoneUTC())

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

      val thirtySecondsAgo = DateTime.now.minusSeconds(30)
      eventually(timeout(Span(30, Seconds))) {
        val lastModified = retrieveCanaryLastModifiedTimestamp()
        withClue(s"Collection did not show a lastModified >= $thirtySecondsAgo. lastModified field was $lastModified") {
          lastModified.value.isAfter(thirtySecondsAgo) should be(true)
        }
      }
    }
  }
}
