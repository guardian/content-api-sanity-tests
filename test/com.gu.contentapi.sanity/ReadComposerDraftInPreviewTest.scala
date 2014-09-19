package com.gu.contentapi.sanity

import org.scalatest.tagobjects.Retryable
import org.scalatest.{Retries, Matchers, FlatSpec}
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import scala.sys.process._
import scala.util.Random
import scala.io.Source
import org.scalatest.time.{Minutes, Seconds, Span}

class ReadComposerDraftInPreviewTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience with Eventually with Retries {

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetryOnFailure (Span(5, Minutes))(super.withFixture(test))
    else
      super.withFixture(test)
  }

  val modifiedHeadline = "Content API Sanity Test " + java.util.UUID.randomUUID.toString
  val uniquePageId = new Random().nextInt.toString


  "GETting the InCopy integration homepage" should "respond with a welcome message" taggedAs (FrequentTest, CODETest) in {
    val httpRequest = request(Config.composerHost + "incopyintegration/index").withHeaders("User-Agent" -> "curl").get
    whenReady(httpRequest) { result => result.body should equal("incopy Integration...")
    }
  }

  "POSTting valid Article XML to the Composer integration Endpoint" should "respond with OK" taggedAs (FrequentTest, CODETest) in {
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
  }

  def importComposerArticle(importEndpoint: String, pathToFileToImport: String): String = {
    val cmd = Seq("curl", "-sS", "-F", "fileData=@" + pathToFileToImport, importEndpoint)
    cmd.!!
  }
}
