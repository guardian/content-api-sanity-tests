package com.gu.contentapi.sanity

import org.scalatest.tagobjects.Retryable
import org.scalatest.time.{Minutes, Seconds, Span}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import scala.io.Source
import org.scalatest.exceptions.TestFailedException
import play.api.libs.ws.WSAuthScheme

class CanaryWritingSanityTest extends SanityTestBase {

  val now = new DateTime()
  val collectionJSON = Source.fromURL(getClass.getResource("/CanaryCollection.json")).getLines().mkString
  val capiDateStamp = now.toString(ISODateTimeFormat.dateTimeNoMillis().withZoneUTC())
  val collectionJSONWithNowTimestamp = collectionJSON.replace("2013-10-15T11:42:17Z", capiDateStamp)

  def doesCanaryHaveUpdatedTimestamp = {
    val httpRequest = requestHost("collections/canary").get()
    whenReady(httpRequest) { result => result.body.contains(capiDateStamp)}
  }

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetryOnFailure (Span(2, Minutes))(super.withFixture(test))
    else
      super.withFixture(test)
  }


  "PUTting and GETting a collection" should "show an updated timestamp" taggedAs Retryable in {
    handleException {
      val putSuccessResponseCode = 202
      val httpRequest = request(Config.writeHost + "collections/canary")
        .withAuth(Config.writeUsername, Config.writePassword, WSAuthScheme.BASIC)
        .withHeaders("Content-Type" -> "application/json")
        .put(collectionJSONWithNowTimestamp)
      whenReady(httpRequest) { result =>
        withClue("Response code was " + result.status + " expected " + putSuccessResponseCode) {
          result.status should equal(putSuccessResponseCode)
        }
        if (result.status == putSuccessResponseCode) {
          eventually(timeout(Span(60, Seconds))) {
            withClue("Collection did not show updated date stamp") {
              doesCanaryHaveUpdatedTimestamp should be(true)
            }
          }
        }
        else {
          throw new TestFailedException("Collection did not post successfully", 1)
        }
      }
    }(fail, testNames.head, tags)
  }
}
