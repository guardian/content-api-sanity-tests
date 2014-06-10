package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import scala.sys.process._

class ReadComposerDraftInPreviewTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "GETting the InCopy integration homepage" should "respond with a welcome message" in {
    val httpRequest = request(Config.composerHost + "incopyintegration/index").withHeaders("User-Agent" -> "curl").get
    whenReady(httpRequest) { result => result.body should equal("incopy Integration...")
    }
  }

    "POSTting valid Article XML to the Composer integration Endpoint" should "respond with OK" in {
      val pathToComposerXML  = getClass.getResource("/composer_article.xml").getFile
      val importEndoint = Config.composerHost + "incopyintegration/article/import"
      val cmd = Seq("curl", "-sS", "-F", "fileData=@"+pathToComposerXML, importEndoint)
      val res = cmd.!!
      val results = res.split(":|;")
      val status = results(0)
      val articleID = results(1)
      status should be ("OK")
      articleID should be ("534561d7e4b0c12b64c7ea50")
    }
}
