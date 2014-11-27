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

package object sanity extends ScalaFutures with Matchers with IntegrationPatience {

  var incidentKeyDateTime: Option[DateTime] = None
  var lastIncidentDateTime: Option[DateTime] = None
  var incidentCount = 0
  val pagerDutyThreshold = 3
  def incrementIncidentCount = {
    incidentCount += 1
    lastIncidentDateTime = Some(DateTime.now)
  }
  def resetCount: Unit ={
    incidentCount = 0
    lastIncidentDateTime = None
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
        //low priority tests are excluded from counter because they run infrequently
        if (isLowPriorityTest(tags, testName)) {
          pagerDutyAlerter(testName, tfe, tags, getIncidentKey)
        }
        else {
          incidentCount match {
            case `pagerDutyThreshold` =>
              println("reporting")
              pagerDutyAlerter(testName, tfe, tags, getIncidentKey)
              resetCount
            case _ =>
              // increment the incident count only if there is an existing recent incident {
              if (lastIncidentDateTime.isEmpty || (Minutes.minutesBetween(lastIncidentDateTime.get, DateTime.now).getMinutes < 10)) {
                incrementIncidentCount
                lastIncidentDateTime = Some(DateTime.now)
              }
              else {
                resetCount
              }
            }
          }
          fail
        }
    }



  def pagerDutyAlerter(testName: String, tfe: TestFailedException, tags: Map[String, Set[String]], incidentKey: String) = {
    val isCODETest = tags.get(testName).map(_.contains("CODETest")).getOrElse(false)
    val serviceKey = if (isLowPriorityTest(tags,testName)) Config.pagerDutyServiceKeyLowPriority else Config.pagerDutyServiceKey
    val environmentInfo = if (isCODETest) "on environment CODE" else ""


    val description = (testName + " failed " + environmentInfo + ", the error reported was: " + tfe.getMessage().take(250) + "...")
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
      val responseStatus = (pagerDutyResponse \ "status").as[String]
      val responseIncidentKey = (pagerDutyResponse \ "incident_key").as[String]
      responseStatus should be("success")
      responseIncidentKey should be(incidentKey)
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