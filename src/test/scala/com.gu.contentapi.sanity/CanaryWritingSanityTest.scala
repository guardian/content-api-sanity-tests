package com.gu.contentapi.sanity

import org.scalatest.{Ignore, Retries, FlatSpec, Matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import com.ning.http.client.Realm.AuthScheme
import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat}
import scala.io.Source
import scala.util.control.Breaks._
import scala.util.{Failure, Try, Success}
import org.scalatest.exceptions.TestFailedException


class CanaryWritingSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience with Retries {

  val now = new DateTime()
  val collectionJSON = Source.fromURL(getClass.getResource("/CanaryCollection.json")).getLines.mkString
  val capiDateStamp = now.toString(ISODateTimeFormat.dateTimeNoMillis().withZoneUTC())
  val collectionJSONWithNowTimestamp = collectionJSON.replace("2013-10-15T11:42:17Z",capiDateStamp)

  "PUTting a Collection" should "return a 202" in {
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

  "GETting the collection" should "show the updated timestamp" in {

    teamCityNotifier("GETting the collection should show updated timestamp", "Collection did not show updated timestamp") {

      val result = retryNTimes(10, 100) {
        val httpRequest = request(Config.host + "collections/canary").get
        whenReady(httpRequest) { result => result.body.contains(capiDateStamp) }
      }

      result should be(true)
    }
  }
}