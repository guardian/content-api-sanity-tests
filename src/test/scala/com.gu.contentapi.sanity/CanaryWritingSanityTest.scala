package com.gu.contentapi.sanity2

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import com.ning.http.client.Realm.AuthScheme
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import scala.io.Source
import org.scalatest.exceptions.TestFailedException



class CanaryWritingSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience with Eventually {


  val now = new DateTime()
  val collectionJSON = Source.fromURL(getClass.getResource("/CanaryCollection.json")).getLines.mkString
  val capiDateStamp = now.toString(ISODateTimeFormat.dateTimeNoMillis().withZoneUTC())
  val collectionJSONWithNowTimestamp = collectionJSON.replace("2013-10-15T11:42:17Z", capiDateStamp)

  "PUTting and GETting a collection" should "show an updated timestamp" taggedAs(FrequentTest, PRODTest)  in {
    val putSuccessResponseCode = 202
    val httpRequest = request(Config.writeHost + "collections/canary")
      .withAuth(Config.writeUsername, Config.writePassword, AuthScheme.BASIC)
      .withHeaders("Content-Type" -> "application/json")
      .put(collectionJSONWithNowTimestamp)
    whenReady(httpRequest) { result =>
      withClue("Response code was " + result.status + " expected " + putSuccessResponseCode) {
        result.status should equal(putSuccessResponseCode)
      }
      if (result.status == putSuccessResponseCode) {
        eventually {
          withClue("Collection did not show updated date stamp") {
            isCAPIShowingChange(Config.host + "collections/canary" + "?api-key=" + Config.apiKey, capiDateStamp) should be (true)
          }
        }
      }
      else {
        throw new TestFailedException("Collection did not post successfully", 1)
      }
    }
  }
}