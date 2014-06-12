package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import scala.sys.process._
import scalax.io.{Resource, Output}
import scala.util.Random
import scala.io.Source

class ReadComposerDraftInPreviewTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  val modifiedHeadline = "Content API Sanity Test " + java.util.UUID.randomUUID.toString
  val uniquePageId = new Random().nextInt.toString


  "GETting the InCopy integration homepage" should "respond with a welcome message" in {
    val httpRequest = request(Config.composerHost + "incopyintegration/index").withHeaders("User-Agent" -> "curl").get
    whenReady(httpRequest) { result => result.body should equal("incopy Integration...")
    }
  }

  "POSTting valid Article XML to the Composer integration Endpoint" should "respond with OK" in {
    val fileToImport = createModifiedXMLTempFile(Source.fromURL(getClass.getResource("/composer_article.xml")).mkString, "story-bundle-placeholder|headline-placeholder|linktext-placeholder|slugword-placeholder", uniquePageId)
    val importEndoint = Config.composerHost + "incopyintegration/article/import"
    val result = importComposerArticle(importEndoint, fileToImport)
    val results = result.split(":|;")
    val status = results(0)
    val articleID = results(1)
   withClue(s"Import was not successful, the endpoint said: $result") {
     status should be("OK")
   }
  }

  def createModifiedXMLTempFile(originalXML: String, originalString: String, replacedString: String): String = {
    val tempFile = java.io.File.createTempFile("TestIntegrationArticleModified-", ".xml")
    val modifiedArticleXML = originalXML.replaceAll(originalString, replacedString)
    val output: Output = Resource.fromFile(tempFile)
    output.write(modifiedArticleXML)
    tempFile.getAbsolutePath
  }

  def importComposerArticle (importEndpoint: String, pathToFileToImport: String): String = {
    val cmd = Seq("curl", "-sS", "-F", "fileData=@" + pathToFileToImport, importEndpoint)
    cmd.!!
  }


}
