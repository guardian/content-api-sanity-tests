package com.gu.contentapi.sanity.support

import com.gu.contentapi.sanity.Config
import org.joda.time.{DateTime, Minutes}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{Matchers, OptionValues}
import play.api.libs.json.{JsValue, Json}

/**
 * Contains all the test failure handling logic:
 *  - counting test failures
 *  - sending PagerDuty alerts
 */
trait TestFailureHandlingSupport extends HttpRequestSupport with Matchers with ScalaFutures with IntegrationPatience with OptionValues {
  import TestFailureHandlingSupport._

  private def isLowPriorityTest(tags: Map[String, Set[String]],testName: String) = {
    tags.get(testName).exists(_.contains("LowPriorityTest"))
  }

  private def getIncidentKey: String = {
    incidentKeyDateTime match {
      case Some(keyTimeStamp) if Minutes.minutesBetween(keyTimeStamp, DateTime.now).getMinutes < 30 => {
        //re-use key if is less than 30 minutes since previous incident
        val key = keyTimeStamp
        key.toString
      }
      case _ => {
        // generate new key at first and after 30 minutes
        val key = DateTime.now
        incidentKeyDateTime = Some(key)
        key.toString
      }
    }
  }

  def handleException(test: => Unit)(fail: => Unit, testName: String, tags: Map[String, Set[String]]) = {
    try {
      test
    } catch {
      case tfe: TestFailedException =>
        Console.err.println(Console.RED + "Test failure: " + tfe.getMessage + Console.RESET)
        //low priority tests are excluded from counter because they run infrequently
        if (isLowPriorityTest(tags, testName)) {
          pagerDutyAlerter(testName, tfe, tags, getIncidentKey)
        }
        else {
          // increment the incident count only if there is an existing recent incident or it is the first incident {
          if (lastIncidentDateTime.isEmpty || (Minutes.minutesBetween(lastIncidentDateTime.get, DateTime.now).getMinutes < RecentIncidentWindowMinutes)) {
            //increment counter
            incrementIncidentCount()
            if (incidentCount == PagerDutyThreshold)
            //report when threshold is met
            {
              pagerDutyAlerter(testName, tfe, tags, getIncidentKey)
              resetToZero()
            }
          }
          else {
            //reset counter if incident is not recent
            resetToOne()
          }
        }
        fail
    }
  }

  def pagerDutyAlerter(testName: String, tfe: TestFailedException, tags: Map[String, Set[String]], incidentKey: String) = {
    try {
      println("Reporting")
      val isCODETest = tags.get(testName).exists(_.contains("CODETest"))
      val serviceKey = if (isLowPriorityTest(tags,testName)) Config.pagerDutyServiceKeyLowPriority else Config.pagerDutyServiceKey
      val environmentInfo = if (isCODETest) "on environment CODE" else ""


      val description = testName + " failed" + environmentInfo + ", the error reported was: " + tfe.getMessage().take(250) + "..."
      val data = Json.obj(
        "service_key" -> serviceKey,
        "event_type" -> "trigger",
        "description" -> description,
        "details" ->  Json.arr(
          Json.obj(
            "name" -> testName,
            "description" -> tfe.getMessage()
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
 * Holds all the state that needs to be shared between all suites.
 */
object TestFailureHandlingSupport {

  /**
   * How long to wait between failed tests before resetting the incident counter
   */
  private val RecentIncidentWindowMinutes = 10

  /**
   * How many tests should fail within a short period of time before we send a PagerDuty alert
   */
  private val PagerDutyThreshold = 3

  private var incidentKeyDateTime: Option[DateTime] = None
  private var lastIncidentDateTime: Option[DateTime] = None
  private var incidentCount = 0

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
