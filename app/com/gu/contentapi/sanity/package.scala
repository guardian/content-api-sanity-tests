package com.gu.contentapi

import java.io.File

import org.joda.time.{Minutes, DateTime}
import org.scalatest.Matchers
import play.api.libs.ws.WSAuthScheme
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.TestFailedException
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.libs.ws.WSRequestHolder
import scalax.file.Path
import scalax.io.{Resource, Output}
import play.api.Play.current
import org.scalatest.OptionValues._

package object sanity extends ScalaFutures with Matchers with IntegrationPatience {

  var incidentKeyDateTime: Option[DateTime] = None
  var lastIncidentDateTime: Option[DateTime] = None
  var incidentCount = 0
  val pagerDutyThreshold = 3
  def incrementIncidentCount = {
    incidentCount += 1
    lastIncidentDateTime = Some(DateTime.now)
    println(s"Incident count: $incidentCount")
  }
  def resetToOne(): Unit ={
    incidentCount = 0
    incrementIncidentCount
  }
  def resetToZero(): Unit ={
    incidentCount = 0
  }

  def isLowPriorityTest(tags: Map[String, Set[String]],testName: String) = {
    (tags.get(testName).map(_.contains("LowPriorityTest")).getOrElse(false))
  }


  def getIncidentKey: String = {
    incidentKeyDateTime match {
      case Some(keyTimeStamp) if (Minutes.minutesBetween(keyTimeStamp, DateTime.now).getMinutes < 30) => {
        //re-use key if is less than 30 minutes since previous incident
       val key = keyTimeStamp
       key.toString()
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
          if (lastIncidentDateTime.isEmpty || (Minutes.minutesBetween(lastIncidentDateTime.get, DateTime.now).getMinutes < 10)) {
            //increment counter
            incrementIncidentCount
            if (incidentCount == pagerDutyThreshold)
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
      val isCODETest = tags.get(testName).map(_.contains("CODETest")).getOrElse(false)
      val serviceKey = if (isLowPriorityTest(tags,testName)) Config.pagerDutyServiceKeyLowPriority else Config.pagerDutyServiceKey
      val environmentInfo = if (isCODETest) "on environment CODE" else ""


      val description = (testName + " failed" + environmentInfo + ", the error reported was: " + tfe.getMessage().take(250) + "...")
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


      val httpRequest =
      request("https://events.pagerduty.com/generic/2010-04-15/create_event.json").post(data)

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

  def request(uri: String): WSRequestHolder = WS.url(uri).withRequestTimeout(10000)

  def isCAPIShowingChange(capiURI: String, modifiedString: String, credentials: Option[(String, String)] = None) = {

    val httpRequest = credentials match {
      case Some((username, password)) =>
        request(capiURI).withAuth(Config.previewUsernameCode, Config.previewPasswordCode, WSAuthScheme.BASIC)
      case None => {
        request(capiURI)
      }
    }
    whenReady(httpRequest.get) { result => result.body.contains(modifiedString)}
  }

  def createModifiedXMLTempFile(originalXML: String, originalString: String, replacedString: String): String = {
    val tempFile = File.createTempFile("TestIntegrationArticleModified-", ".xml")
    val modifiedArticleXML = originalXML.replaceAll(originalString, replacedString)
    val output: Output = Resource.fromFile(tempFile)
    output.write(modifiedArticleXML)
    tempFile.getAbsolutePath
  }

  def deleteFileIfExists(filePath: String) {
    val tempFilePath: Path = Path.fromString(filePath)
    tempFilePath.deleteIfExists()
  }

  def requestHost(path: String) =
  // make sure query string is included
    if (path.contains("?"))
      request(Config.host + path + "&api-key=" + Config.apiKey)
    else
      request(Config.host + path + "?api-key=" + Config.apiKey)
}