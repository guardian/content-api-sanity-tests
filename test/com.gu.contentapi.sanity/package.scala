package com.gu.contentapi

import org.scalatest.Matchers
import play.api.libs.ws.WS
import play.api.libs.ws.WSRequestHolder
import play.api.libs.ws.WSAuthScheme
import org.scalatest.concurrent.ScalaFutures
import scalax.io.{Resource, Output}
import scalax.file.Path
import java.io.File
import play.api.Play.current


package object sanity extends ScalaFutures with Matchers {

  //see http://stackoverflow.com/questions/24881145/how-do-i-use-play-ws-library-in-normal-sbt-project-instead-of-play
  val builder = new com.ning.http.client.AsyncHttpClientConfig.Builder
  val client = new play.api.libs.ws.ning.NingWSClient(builder.build)


  def request(uri: String):WSRequestHolder = client.url(uri).withRequestTimeout(10000)

  def isCAPIShowingChange(capiURI: String, modifiedString: String, credentials: Option[(String, String)] = None) = {

    val httpRequest = credentials match {
      case Some((username, password)) =>
        request(capiURI).withAuth(Config.previewUsernameCode, Config.previewPasswordCode, WSAuthScheme.BASIC)
      case None => {
        request(capiURI)
      }
    }
    whenReady(httpRequest.get) { result =>
      withClue("Authentication failed, check credentials") {
        result.status should not equal (401)
      }
      result.body.contains(modifiedString)}
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
