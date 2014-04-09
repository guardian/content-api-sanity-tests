package com.gu.contentapi.sanity

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import com.typesafe.config.ConfigFactory
import org.scalatest.time.{Second, Span}
import com.ning.http.client.Realm.AuthScheme
import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat}

class CanaryWritingSanityTest extends FlatSpec with Matchers with ScalaFutures {

  implicit val defaultPatience = PatienceConfig(timeout = Span(1, Second), interval = Span(1, Second))
   val now = new DateTime();
   val CAPIDateStamp = now.toString(ISODateTimeFormat.dateTimeNoMillis().withZoneUTC())

  val collectionJSON=s"""  |{
                       |               "type": "features",
                       |               "title": "Canary in the coalmine",
                       |               "groups": [
                       |                   "Main content",
                       |                   "Other articles"
                       |               ],
                       |               "curatedContent": [
                       |                   {
                       |                       "id": "world/2013/dec/19/timor-leste-asks-un-court-to-order-australia-return-seized-documents",
                       |                       "metadata": {
                       |                           "trailText": "Asio raids on legal office in Canberra violated sovereignty of Dili",
                       |                           "headline": "Timor-Leste asks UN court to order Australia to return seized documents",
                       |                           "supportingContent": [
                       |                               {
                       |                                   "id": "music/2013/dec/19/angel-haze-album-released-digitally",
                       |                                   "metadata": {
                       |                                       "headline": "Scarlett Johansson is right: SodaStream does not fit with Oxfam"
                       |                                   }
                       |                               },
                       |                               {
                       |                                   "id": "world/2013/dec/04/australia-secret-service-raids-lawyer-and-former-spy"
                       |                               }
                       |                           ],
                       |                           "imageAdjustment": "hide",
                       |                           "group": 0
                       |                       }
                       |                   },
                       |                   {
                       |                       "id": "environment/video/2013/jul/17/japan-international-court-whaling-video",
                       |                       "metadata": {
                       |                           "group": 1
                       |                       }
                       |                   }
                       |               ],
                       |               "backfill": "world",
                       |               "lastModified": "$CAPIDateStamp"
                       |               ,"modifiedBy": "Gideon Goldberg"
          }""".stripMargin
  val conf = ConfigFactory.load()
  val sanityConfig = conf.getConfig("content-api-sanity-tests")
  conf.checkValid(ConfigFactory.defaultReference(), "content-api-sanity-tests")

  "PUTting a Collection" should "return a 202" in{
  val httpRequest = request(sanityConfig.getString("write-host")+"collections/canary")
    .withAuth(sanityConfig.getString("write-username"),sanityConfig.getString("write-password"),AuthScheme.BASIC)
    .withHeaders("Content-Type" -> "application/json")
    .put(collectionJSON)

  whenReady(httpRequest) { result =>
    result.status should equal(202)
    }
  }



}
