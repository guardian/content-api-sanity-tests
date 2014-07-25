package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import scala.sys.process._
import scala.util.Random
import scala.io.Source
import org.scalatest.time.{Seconds, Span}


class ReadComposerDraftInPreviewTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience with Eventually {

  val modifiedHeadline = "Content API Sanity Test " + java.util.UUID.randomUUID.toString
  val uniquePageId = new Random().nextInt.toString


  "An article POSTed to the Composer integration Endpoint" should "appear in the Preview API" taggedAs(FrequentTest, CODETest, LowPriorityTest) in {
    handleException {

      val httpRequest = request(Config.composerHost + "incopyintegration/index").withHeaders("User-Agent" -> "curl").get
      whenReady(httpRequest)
      {
        result =>
          withClue("Composer healthcheck failed, endpoint said " + result.body) {
            result.body should equal("incopy Integration...")
          }
      }
      lazy val fileToImport = createModifiedXMLTempFile(Source.fromURL(getClass.getResource("/composer_article.xml")).mkString, "story-bundle-placeholder|headline-placeholder|linktext-placeholder|slugword-placeholder", uniquePageId)

      val importEndpoint = Config.composerHost + "incopyintegration/article/import"
      val result = importComposerArticle(importEndpoint, fileToImport)
      deleteFileIfExists(fileToImport)
      val results = result.split(":|;")
      val status = results(0)
      val articleID = results(1)
      val composerItemEndpointURI = Config.previewHostCode + "internal-code/composer/" + articleID
      val lastModifiedSearchURI = Config.previewHostCode + "search?use-date=last-modified"
      withClue(s"Import was not successful, the endpoint said: $result") {
        status should be("OK")
      }
      eventually(timeout(Span(60, Seconds))) {
        withClue(s"Composer article was not found at: $composerItemEndpointURI within 60 seconds") {
          isCAPIShowingChange(composerItemEndpointURI, uniquePageId, Some(Config.previewUsernameCode: String, Config.previewPasswordCode: String)) should be(true)
        }
      }

      eventually(timeout(Span(60, Seconds))) {
        withClue(s"Composer article was not $articleID was not found at: $lastModifiedSearchURI within 60 seconds") {
          isCAPIShowingChange(lastModifiedSearchURI, uniquePageId, Some(Config.previewUsernameCode: String, Config.previewPasswordCode: String)) should be(true)
        }
      }
    }(fail, testNames.head, tags)
  }

  def importComposerArticle(importEndpoint: String, pathToFileToImport: String): String = {
    val cmd = Seq("curl", "-sS", "-F", "fileData=@" + pathToFileToImport, importEndpoint)
    cmd.!!
  }
}
