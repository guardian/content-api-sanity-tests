package com.gu.contentapi.sanity

import org.joda.time.{DateTime}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{JsValue, Json}
import org.scalatest.OptionValues._

class CrosswordsIndexingTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "A new Crossword" should "be indexed every day" taggedAs(LowPriorityTest) in {

    handleException {
      val now = DateTime.now
      val isAfterChristmas = now.getMonthOfYear == 12 && (now.getDayOfMonth == 26 || now.getDayOfMonth == 27)
      val twentyFiveHoursAgo = now.minusHours(25)
      assume(!isAfterChristmas, "Cancelling as Crosswords are not always published over the Christmas period")

      val httpRequest = requestHost("search?tag=type/crossword&order-by=newest").get
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val newestItemOpt = (json \ "response" \ "results")(0).asOpt[JsValue]
        withClue("First item not found in response") {
          newestItemOpt should be (defined)
        }
        newestItemOpt foreach { newestItem =>
          val newestItemDateString = (newestItem \ "webPublicationDate").asOpt[String]
          val newestItemDate = DateTime.parse(newestItemDateString.value)
          withClue(s"Latest indexed crossword was at $newestItemDate which is more than 25 hours ago ($twentyFiveHoursAgo)") {
            newestItemDate.isAfter(twentyFiveHoursAgo) should be(true)
          }
        }
      }
    }(fail, testNames.head, tags)
  }
}


