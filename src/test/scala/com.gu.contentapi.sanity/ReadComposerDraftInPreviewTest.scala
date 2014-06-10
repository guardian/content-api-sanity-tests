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
    val fileToImport = createModifiedXML(Source.fromURL(getClass.getResource("/composer_article.xml")).mkString, "7348690|british-gunmakers-arms-sri-lanka|placeholder", uniquePageId)
    val importEndoint = Config.composerHost + "incopyintegration/article/import"
    val cmd = Seq("curl", "-sS", "-F", "fileData=@" + fileToImport, importEndoint)
    val res = cmd.!!
    val results = res.split(":|;")
    val status = results(0)
    val articleID = results(1)
    status should be("OK")
  }

  def createModifiedXML(originalXML: String, originalString: String, replacedString: String): String = {
    val tempFile = java.io.File.createTempFile("TestIntegrationArticleModified-", ".xml")
    val modifiedArticleXML = originalXML.replaceAll(originalString, replacedString)
    val output: Output = Resource.fromFile(tempFile)
    output.write(modifiedArticleXML)
    tempFile.getAbsolutePath
  }
}
