package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import scala.io.Source
import java.io.File
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import com.ning.http.multipart.{StringPart, FilePart, MultipartRequestEntity, Part}
import com.ning.http.client.{Realm, AsyncHttpClientConfig, AsyncHttpClient}
import java.nio.file.Paths


class ReadComposerDraftInPreviewTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Posting an article into Composer" should "respond with OK" in {
    val httpRequest = request(Config.composerHost + "/incopyintegration/index").withHeaders("User-Agent" -> "curl").get
    whenReady(httpRequest) { result => result.body should equal("incopy Integration...")
    }

    val file  = getClass.getResource("/composer_article.xml").getFile







  }
}
