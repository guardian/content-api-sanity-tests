package com.gu.contentapi.sanity

import org.scalatest.{Retries, FlatSpec, Matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.time.{Seconds, Second, Span}
import com.ning.http.client.Realm.AuthScheme
import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat}
import org.scalatest.concurrent.Eventually._
import scala.io.Source
import scala.io.Source
import org.scalatest.tagobjects.Retryable


class CanaryWritingSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience with Retries {

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetry { super.withFixture(test) }
    else
      super.withFixture(test)
  }

  val now = new DateTime()
  val collectionJSON = Source.fromURL(getClass.getResource("/CanaryCollection.json")).getLines.mkString
  val capiDateStamp = now.toString(ISODateTimeFormat.dateTimeNoMillis().withZoneUTC())
  val collectionJSONWithNowTimestamp = collectionJSON.replace("2013-10-15T11:42:17Z",capiDateStamp)

  "PUTting a Collection" should "return a 202" taggedAs(Retryable) in {
    val httpRequest = request(Config.writeHost + "collections/canary")
      .withAuth(Config.writeUsername,Config.writePassword,AuthScheme.BASIC)
      .withHeaders("Content-Type" -> "application/json")
      .put(collectionJSONWithNowTimestamp)
      teamCityNotifier("PUTting a collection should return a 202","Did not return expected response code of 202, see build log for full details") {
    whenReady(httpRequest) { result =>
        result.status should equal(202)
      }
    }
  }

  "GETting the collection" should "show the updated timestamp" taggedAs(Retryable) in {
    val httpRequest = request(Config.host + "collections/canary").get
    teamCityNotifier("GETting the collection should show updated timestamp","Collection did not show updated timestamp") {
      whenReady(httpRequest) { result =>
        result.body should include (capiDateStamp)
      }
    }
  }
}