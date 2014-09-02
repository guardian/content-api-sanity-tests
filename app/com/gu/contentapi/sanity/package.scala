package com.gu.contentapi

import java.io.File
import com.ning.http.client.Realm.AuthScheme
import org.joda.time.{Minutes, Period, DateTime}
import org.scalatest.Matchers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder
import scalax.file.Path
import scalax.io.{Resource, Output}

package object sanity extends ScalaFutures with Matchers with IntegrationPatience {

  def handleException(test: =>Unit)(fail: =>Unit, testName: String, tags: Map[String, Set[String]])= {
    try {
      test
    } catch {
      case tfe: TestFailedException =>
        val isExistingRecentIncident= new Period(Minutes.minutesBetween(previousIncident.dateTime, new DateTime)).getMinutes < 30
       val incident = if(isExistingRecentIncident) pagerDutyAlerter(testName, tfe, tags, None) else pagerDutyAlerter(testName, tfe, tags, previousIncident.key)

      lazy val previousIncident = incident
        fail
    }
  }

  def pagerDutyAlerter(testName: String, tfe: TestFailedException, tags: Map[String, Set[String]], incident: Option[Incident]): Incident =  {
    //val isExistingRecentIncident=(new Period(Minutes.minutesBetween(currentIncident._2, new DateTime).getMinutes))
    val isLowPriority = tags.get(testName).map(_.contains("LowPriorityTest")).getOrElse(false)
    val isCODETest = tags.get(testName).map(_.contains("CODETest")).getOrElse(false)
    val isExistingIncident = incident.isDefined

    val serviceKey = if (isLowPriority) Config.pagerDutyServiceKeyLowPriority else Config.pagerDutyServiceKey

    val environmentInfo = if (isCODETest) "on environment CODE" else ""


    val description = (testName + " failed " + environmentInfo + ", the error reported was: " + tfe.getMessage().take(250) + "...")
    val dataWithIncidentKey = (
      "service_key" -> serviceKey,
      "incident_key" -> incident.get.key,
      "event_type" -> "trigger",
      "description" -> description,
      "client" -> "Content API Sanity Tests",
      "client_url" -> "https://github.com/guardian/content-api-sanity-tests"
      )

    val data = Json.obj(
      "service_key" -> serviceKey,
      "event_type" -> "trigger",
      "description" -> description,
      "client" -> "Content API Sanity Tests",
      "client_url" -> "https://github.com/guardian/content-api-sanity-tests"
    )
    val httpRequest = if(isExistingIncident) request("https://events.pagerduty.com/generic/2010-04-15/create_event.json").post(dataWithIncidentKey) else request("https://events.pagerduty.com/generic/2010-04-15/create_event.json").post(data)

    val currentIncident = whenReady(httpRequest) { result =>
      val pagerDutyResponse: JsValue = Json.parse(result.body)
      val status = (pagerDutyResponse \ "status").as[String]
      val incidentKey = (pagerDutyResponse \ "incident_key").as[String]
      status should be("success")
      Incident(incidentKey,new DateTime)
    }
    currentIncident
  }
  def request(uri: String):WSRequestHolder = WS.url(uri).withRequestTimeout(10000)

  def isCAPIShowingChange(capiURI: String, modifiedString: String, credentials: Option[(String, String)] = None) = {

    val httpRequest = credentials match {
      case Some((username, password)) =>
        request(capiURI).withAuth(Config.previewUsernameCode, Config.previewPasswordCode, AuthScheme.BASIC)
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

  case class Incident(key: String, dateTime: DateTime)
}
