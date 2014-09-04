package com.gu.contentapi

import java.io.File
import com.ning.http.client.Realm.AuthScheme
import org.joda.time.{Seconds, Minutes, DateTime}
import org.scalatest.Matchers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder
import scalax.file.Path
import scalax.io.{Resource, Output}

package object sanity extends ScalaFutures with Matchers with IntegrationPatience {

 var incidentKeyDateTime: Option[DateTime] = None

  def handleException(test: =>Unit)(fail: =>Unit, testName: String, tags: Map[String, Set[String]])= {
    try {
      test
    } catch {
      case tfe: TestFailedException =>
        //first run

def generateOrReuseIncidentKey:String = {
  if (!incidentKeyDateTime.isDefined){
    //generate incident key
    incidentKeyDateTime = Some(DateTime.now)
    incidentKeyDateTime.get.toString
  }
  else if (Minutes.minutesBetween(incidentKeyDateTime.get, DateTime.now).getMinutes < 30) {
    //re-use key if is less than 30 minutes since previous incident
    incidentKeyDateTime.get.toString
  }
  else
  {
    //after 30 minutes generate new key
    incidentKeyDateTime = Some(DateTime.now)
    incidentKeyDateTime.get.toString
  }

}


     pagerDutyAlerter(testName, tfe, tags, generateOrReuseIncidentKey)

     fail
    }
  }

  def pagerDutyAlerter(testName: String, tfe: TestFailedException, tags: Map[String, Set[String]], incidentKey: String)=  {
    //val isExistingRecentIncident=(new Period(Minutes.minutesBetween(currentIncident._2, new DateTime).getMinutes))
    val isLowPriority = tags.get(testName).map(_.contains("LowPriorityTest")).getOrElse(false)
    val isCODETest = tags.get(testName).map(_.contains("CODETest")).getOrElse(false)

   // val recentIncident = incident. (i => new Period(Minutes.minutesBetween(i.dateTime, new DateTime)).getMinutes < 30)

    val serviceKey = if (isLowPriority) Config.pagerDutyServiceKeyLowPriority else Config.pagerDutyServiceKey

    val environmentInfo = if (isCODETest) "on environment CODE" else ""


    val description = (testName + " failed " + environmentInfo + ", the error reported was: " + tfe.getMessage().take(250) + "...")
    //val incidentKey: String= recentIncident.map(i => i.dateTime.toString).getOrElse("")
    val data = Json.obj (
      "service_key" -> serviceKey,
      "event_type" -> "trigger",
      "description" -> "TEST please ignore", //description
      "client" -> "Content API Sanity Tests",
      "client_url" -> "https://github.com/guardian/content-api-sanity-tests",
      "incident_key" -> incidentKey
      )

    val httpRequest =
      request("https://events.pagerduty.com/generic/2010-04-15/create_event.json").post(data)

    whenReady(httpRequest) { result =>
      val pagerDutyResponse: JsValue = Json.parse(result.body)
      val status = (pagerDutyResponse \ "status").as[String]
      val incidentKey = (pagerDutyResponse \ "incident_key").as[String]
      status should be("success")
      //incidentKey should be(incident.dateTime.toString)
      println("incident key is: "+incidentKey)
      println("status is: "+incidentKey)
     println("response is: "+ result.body)
     // Incident(new DateTime)
    }
   // recentIncident.getOrElse(currentIncident)
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
