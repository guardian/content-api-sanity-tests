package com.gu.contentapi

import java.io.File
import com.ning.http.client.Realm.AuthScheme
import org.scalatest.Matchers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder
import scalax.file.Path
import scalax.io.{Resource, Output}

package object sanity extends ScalaFutures with Matchers with IntegrationPatience {

  def handleException(test: =>Unit)(fail: =>Unit, testName: String, isLowPriority: Boolean = false)= {
    try {
      test
    } catch {
      case tfe: TestFailedException =>
        pagerDutyAlerter(testName, tfe, isLowPriority)
        fail
    }
  }
  def pagerDutyAlerter(testName: String, tfe: TestFailedException, isLowPriority: Boolean){
    val serviceKey = if(isLowPriority) Config.pagerDutyServiceKeyLowPriority else Config.pagerDutyServiceKey


    val description = (testName + " failed, the error reported was: " + tfe.getMessage().take(250) + "...")

    val data = Json.obj(
      "service_key" -> serviceKey,
      "event_type" -> "trigger",
      "description" -> description,
      "client" -> "Content API Sanity Tests",
      "client_url" -> "https://github.com/guardian/content-api-sanity-tests"
    )
    val httpRequest = request("https://events.pagerduty.com/generic/2010-04-15/create_event.json").post(data)
    whenReady(httpRequest) { result =>
      result.body should include("success")}

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
}
