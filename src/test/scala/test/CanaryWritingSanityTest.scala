package com.gu.contentapi.sanity

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Second, Span}
import com.ning.http.client.Realm.AuthScheme
import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat}
import org.scalatest.concurrent.Eventually._
import scala.io.Source


class CanaryWritingSanityTest extends FlatSpec with Matchers with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(1, Second))
  val now = new DateTime()
  val collectionJSON = Source.fromFile("src/test/resources/CanaryCollection.json").getLines.mkString
  val capiDateStamp = now.toString(ISODateTimeFormat.dateTimeNoMillis().withZoneUTC())
  val collectionJSONWithNowTimestamp = collectionJSON.replace("2013-10-15T11:42:17Z",capiDateStamp)

  "PUTting a Collection" should "return a 202" in {
    val httpRequest = request(Config.writeHost+"collections/canary")
      .withAuth(Config.writeUsername,Config.writePassword,AuthScheme.BASIC)
      .withHeaders("Content-Type" -> "application/json")
      .put(collectionJSONWithNowTimestamp)

    whenReady(httpRequest) { result =>
      result.status should equal(202)
    }
  }

  "GETting the collection" should "show the updated timestamp" in {
    eventually(timeout(Span(5, Seconds)), interval(Span(1, Second))) {
      val httpRequest = request(Config.host+"collections/canary").get()
      whenReady(httpRequest) { result =>
      result.body should include (capiDateStamp)
      }
    }
  }
}