package com.gu.contentapi.sanity

import org.joda.time.{DateTime, Minutes}

import scala.concurrent.duration._
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.Play.current

class FacebookInstantArticlesIsPublishingTest(context: Context) extends SanityTestBase(context) {

  "Facebook instant articles" should "have published something in the last 30 minutes" in {
    val httpRequest = WS.url(s"${Config.facebookInstantArticlesHost}healthcheck/publication").withRequestTimeout(10000.millis).get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Cannot access publication healthcheck endpoint for FB IA")
      val json = Json.parse(result.body)
      (json \ "lastPublished").toOption.map{ lastPublished =>
        val lastPublishedDateTime = new DateTime(lastPublished.toString)
        Minutes.minutesBetween(lastPublishedDateTime, new DateTime()).isLessThan(Minutes.minutes(30)) should be(true)
      }
    }
  }

}

