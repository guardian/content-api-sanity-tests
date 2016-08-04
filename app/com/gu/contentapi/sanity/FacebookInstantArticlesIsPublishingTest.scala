package com.gu.contentapi.sanity

import org.joda.time.{DateTime, DateTimeConstants, Minutes}

import scala.concurrent.duration._
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.Play.current

class FacebookInstantArticlesIsPublishingTest(context: Context) extends SanityTestBase(context) {

  "Facebook instant articles" should "have published something in the last 30 minutes (60 minutes on weekends or between 00:00 - 08:30)" in {
    val httpRequest = WS.url(s"${Config.facebookInstantArticlesHost}healthcheck/publication").withRequestTimeout(10000.millis).get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Cannot access publication healthcheck endpoint for FB IA")
      val json = Json.parse(result.body)
      (json \ "lastPublished").toOption.foreach { lastPublished =>
        val lastPublishedDateTime = new DateTime(lastPublished.as[String])

        val now = DateTime.now()
        val twelveAM = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
        val eightThirtyAM = now.withHourOfDay(8).withMinuteOfHour(30).withSecondOfMinute(0)

        if (now.dayOfWeek.get == DateTimeConstants.SATURDAY ||
            now.dayOfWeek.get == DateTimeConstants.SUNDAY ||
            now.isBefore(eightThirtyAM) && now.isAfter(twelveAM))
        {
          Minutes.minutesBetween(lastPublishedDateTime, now).isLessThan(Minutes.minutes(60)) should be(true)
        } else {
          Minutes.minutesBetween(lastPublishedDateTime, now).isLessThan(Minutes.minutes(30)) should be(true)
        }
      }
    }
  }

}

