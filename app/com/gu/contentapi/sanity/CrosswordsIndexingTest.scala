package com.gu.contentapi.sanity

import java.text.SimpleDateFormat

import org.joda.time.{LocalDate, DateTime}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json

class CrosswordsIndexingTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "A new Crossword" should "be indexed every day" taggedAs(LowPriorityTest, PRODTest) in {

    handleException {
      val now = DateTime.now
      val isAfterChristmas = now.getMonthOfYear == 12 && (now.getDayOfMonth == 26 || now.getDayOfMonth == 27)
      val twentyFiveHoursAgo = now.minusHours(25)
      val httpRequest = requestHost("search?tag=type/crossword&order-by=newest").get
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val newestItem = (json \ "response" \ "results")(0)
        val newestItemDateString = (newestItem \ "webPublicationDate").as[String]
        val newestItemDate = DateTime.parse(newestItemDateString)
        assume(!isAfterChristmas, "Cancelling as Crosswords are not always published over the Christmas period")
        withClue(s"Latest indexed crossword was at $newestItemDate which is more than 25 hours ago ($twentyFiveHoursAgo)") {
          newestItemDate.isAfter(twentyFiveHoursAgo) should be(true)
        }
      }
    }(fail, testNames.head, tags)
  }
}


