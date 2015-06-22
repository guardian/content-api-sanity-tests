package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.DoNothing
import org.joda.time.DateTime
import org.scalatest.DoNotDiscover

import scala.concurrent.duration._

/**
 * Checks that api-indexer's clock is in sync with the sanity-tests machine's clock (which is set using ntp).
 *
 * Note that this test will only run in TeamCity (i.e. it's like a CODE test),
 * but it will talk to the PROD api-indexer machines.
 * It can't run in EC2 like the other PROD tests because it needs access to the data centre.
 */
@DoNotDiscover
class ApiIndexerClockDriftTest extends SanityTestBase(testFailureHandler = DoNothing) {

  val DriftThreshold = 5.seconds

  private def retrieveCurrentTime(baseUrl: String): DateTime = {
    val httpResponse = request(s"${baseUrl}api/time/current").get()
    whenReady(httpResponse) { result =>
      new DateTime(result.body)
    }
  }

  private def checkForClockDrift(baseUrl: String): Unit = {
    import org.joda.time.Seconds
    val currentTimeHere = DateTime.now()
    val currentTimeThere = retrieveCurrentTime(Config.apiIndexerLiveHost)
    val difference = Seconds.secondsBetween(currentTimeHere, currentTimeThere).getSeconds.abs
    withClue(s"api-indexer thinks it's currently $currentTimeThere but I think it's $currentTimeHere") {
      difference should be < DriftThreshold.toSeconds.toInt
    }
  }

  "Live api-indexer" should "know what time it is" in {
    checkForClockDrift(Config.apiIndexerLiveHost)
  }

  "Preview api-indexer" should "know what time it is" in {
    checkForClockDrift(Config.apiIndexerPreviewHost)
  }

}
