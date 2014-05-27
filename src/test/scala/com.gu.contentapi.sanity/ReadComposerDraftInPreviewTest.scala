package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import scala.io.Source

class ReadComposerDraftInPreviewTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Posting an article into Composer" should "respond with OK" in {
    val httpRequest = request(Config.composerHost + "/incopyintegration/index").withHeaders("User-Agent" -> "curl").get
    whenReady(httpRequest) { result => result.body should equal("incopy Integration...")
      val postArticle = request(Config.composerHost + "incopyintegration/article/import").withHeaders("User-Agent" -> "curl").post(Source.fromURL(getClass.getResource("/composer_article.xml")).getLines.mkString)
      whenReady(postArticle) { result =>
        result.status should equal(200 )
      }
    }
  }
}
