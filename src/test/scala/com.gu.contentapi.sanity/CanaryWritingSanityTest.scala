package com.gu.contentapi.sanity

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import com.ning.http.client.Realm.AuthScheme
import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat}
import scala.io.Source
import org.scalatest.exceptions.TestFailedException


class CanaryWritingSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  val now = new DateTime()
  val collectionJSON = Source.fromURL(getClass.getResource("/CanaryCollection.json")).getLines.mkString
  val capiDateStamp = now.toString(ISODateTimeFormat.dateTimeNoMillis().withZoneUTC())
  val collectionJSONWithNowTimestamp = collectionJSON.replace("2013-10-15T11:42:17Z", capiDateStamp)

  "PUTting and GETting a collection" should "show an updated timestamp" in {
    val httpRequest = request(Config.writeHost + "collections/canary")
      .withAuth(Config.writeUsername, Config.writePassword, AuthScheme.BASIC)
      .withHeaders("Content-Type" -> "application/json")
      .put(collectionJSONWithNowTimestamp)
    whenReady(httpRequest) { result =>
      withClue(s"Response code was " + result.status + " expected 202") {
        result.status should equal (202)
      }
      if (result.status == 202) {
        val numOfAttempts = 10

        val doesCanaryHaveUpdatedTimestamp = retryNTimes(numOfAttempts, 1000) {
          val httpRequest = requestHost("collections/canary").get
          whenReady(httpRequest) { result => result.body.contains(capiDateStamp)}
        }
        withClue(s"Collection did not show updated date stamp after $numOfAttempts attempts") {
          doesCanaryHaveUpdatedTimestamp should be(true)
        }
      }
      else {
        throw new TestFailedException("Collection did not post successfully", 1)
      }
    }
  }
}