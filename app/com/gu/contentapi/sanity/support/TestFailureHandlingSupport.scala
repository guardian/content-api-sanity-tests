package com.gu.contentapi.sanity.support

import com.gu.contentapi.sanity.Config
import org.joda.time.{DateTime, Minutes}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import org.scalatest.matchers.should.Matchers

/**
 * Contains all the test failure handling logic:
 *  - counting test failures
 *  - sending PagerDuty alerts
 */
trait TestFailureHandlingSupport extends TestSuite {

  def testFailureHandler: TestFailureHandler

  override protected def withFixture(test: NoArgTest): Outcome = {
    val outcome = super.withFixture(test)
    outcome match {
      case Failed(e) => testFailureHandler.handleTestFailure(test.name, e, tags.getOrElse(test.name, Set.empty))
      case _ => // do nothing
    }
    outcome
  }

}

trait TestFailureHandler {

  def handleTestFailure(testName: String, exception: Throwable, tags: Set[String])

}

abstract class PagerDutyAlertingTestFailureHandler(override val wsClient: WSClient)
  extends TestFailureHandler with HttpRequestSupport with Matchers with ScalaFutures with IntegrationPatience with OptionValues {
  import PagerDutyAlertingTestFailureHandler._

  protected def latestIncidentKey(): String = {
    incidentKeyDateTime match {
      case Some(keyTimeStamp) if Minutes.minutesBetween(keyTimeStamp, DateTime.now).getMinutes < 30 =>
        //re-use key if is less than 30 minutes since previous incident
        val key = keyTimeStamp
        key.toString
      case _ =>
        // generate new key at first and after 30 minutes
        val key = DateTime.now
        incidentKeyDateTime = Some(key)
        key.toString
    }
  }

  protected def sendPagerDutyAlert(testName: String, exception: Throwable, tags: Set[String], incidentKey: String) = {
    try {
      println("Reporting")
      val isLowPriorityTest = tags.contains("LowPriorityTest")
      val isCODETest = tags.contains("CODETest")
      val serviceKey = if (isLowPriorityTest) Config.pagerDutyServiceKeyLowPriority else Config.pagerDutyServiceKey
      val environmentInfo = if (isCODETest) "on environment CODE" else ""

      val description = testName + " failed" + environmentInfo + ", the error reported was: " + exception.getMessage.take(250) + "..."
      val data = Json.obj(
        "service_key" -> serviceKey,
        "event_type" -> "trigger",
        "description" -> description,
        "details" ->  Json.arr(
          Json.obj(
            "name" -> testName,
            "description" -> exception.getMessage
          )
        ),
        "client" -> "Content API Sanity Tests",
        "client_url" -> "https://github.com/guardian/content-api-sanity-tests",
        "incident_key" -> incidentKey
      )

      val httpRequest = request("https://events.pagerduty.com/generic/2010-04-15/create_event.json").post(data)

      whenReady(httpRequest) { result =>
        val pagerDutyResponse: JsValue = Json.parse(result.body)
        val responseStatus = (pagerDutyResponse \ "status").asOpt[String]
        val responseIncidentKey = (pagerDutyResponse \ "incident_key").asOpt[String]
        responseStatus.value should be("success")
        responseIncidentKey.value should be(incidentKey)
      }
    } catch {
      case e: Exception => Console.err.println(Console.RED + "PagerDuty reporting failed with exception: " + e.getMessage + Console.RESET)
    }

  }
}

/**
 * Holds state about PagerDuty incident keys that needs to be shared across multiple suites
 * and even across multiple runs of the scheduler.
 */
object PagerDutyAlertingTestFailureHandler {
  private var incidentKeyDateTime: Option[DateTime] = None
}

/**
 * Handler that sends PagerDuty alerts only when a few test failures occur within a few minutes of each other.
 *
 * Note that this handler holds state about failure counts that needs to be shared across multiple suites
 * and even across multiple runs of the scheduler.
 */
class FrequentScheduledTestFailureHandler(wsClient: WSClient) extends PagerDutyAlertingTestFailureHandler(wsClient) {

  /**
   * How long to wait between failed tests before resetting the incident counter
   */
  private val RecentIncidentWindowMinutes = 10

  /**
   * How many tests should fail within a short period of time before we send a PagerDuty alert
   */
  private val PagerDutyThreshold = 3

  private var lastIncidentDateTime: Option[DateTime] = None
  private var incidentCount = 0

  override def handleTestFailure(testName: String, exception: Throwable, tags: Set[String]): Unit = {
    Console.err.println(Console.RED + "Test failure: " + exception.getMessage + Console.RESET)
    // increment the incident count only if there is an existing recent incident or it is the first incident {
    if (lastIncidentDateTime.isEmpty || (Minutes.minutesBetween(lastIncidentDateTime.get, DateTime.now).getMinutes < RecentIncidentWindowMinutes)) {
      //increment counter
      incrementIncidentCount()
      if (incidentCount == PagerDutyThreshold) {
        //report when threshold is met
        sendPagerDutyAlert(testName, exception, tags, latestIncidentKey())
        resetToZero()
      }
    }
    else {
      //reset counter if incident is not recent
      resetToOne()
    }
  }

  private def incrementIncidentCount() = {
    incidentCount += 1
    lastIncidentDateTime = Some(DateTime.now)
    println(s"Incident count: $incidentCount")
  }
  private def resetToOne(): Unit ={
    incidentCount = 0
    incrementIncidentCount()
  }
  private def resetToZero(): Unit ={
    incidentCount = 0
  }
}

/**
 * Handler that sends a PagerDuty alert immediately
 */
class InfrequentScheduledTestsFailureHandler(wsClient: WSClient) extends PagerDutyAlertingTestFailureHandler(wsClient) {

  override def handleTestFailure(testName: String, exception: Throwable, tags: Set[String]): Unit = {
    Console.err.println(Console.RED + "Test failure: " + exception.getMessage + Console.RESET)
    sendPagerDutyAlert(testName, exception, tags, latestIncidentKey())
  }

}

object DoNothingTestFailureHandler extends TestFailureHandler {
  override def handleTestFailure(testName: String, exception: Throwable, tags: Set[String]): Unit = {}
}