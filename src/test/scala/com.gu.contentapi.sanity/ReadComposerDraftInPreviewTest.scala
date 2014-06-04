package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import scala.io.Source
import java.io.File
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import com.ning.http.multipart.{StringPart, FilePart, MultipartRequestEntity, Part}
import com.ning.http.client.{Realm, AsyncHttpClientConfig, AsyncHttpClient}


class ReadComposerDraftInPreviewTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Posting an article into Composer" should "respond with OK" in {
    val httpRequest = request(Config.composerHost + "/incopyintegration/index").withHeaders("User-Agent" -> "curl").get
    whenReady(httpRequest) { result => result.body should equal("incopy Integration...")
    }


    //val filename = getClass.getResource("/composer_article.xml").toURI

    var realm = new Realm.RealmBuilder().build()
    var cfg = new AsyncHttpClientConfig.Builder().build()
    val asyncHttpClient = new AsyncHttpClient(cfg)
   val url = Config.composerHost + "incopyintegration/article/import"
   val ahc = asyncHttpClient.preparePost( url)

    ahc.setRealm(realm)
    ahc.setHeader("User-Agent", "curl")
    ahc.setHeader("Content-Type", "multipart/form-data")

    ahc.addBodyPart( new com.ning.http.client.FilePart("composer_article.xml", new File(getClass.getResource("/composer_article.xml").toURI), "application/xml", "UTF-8"))

    ahc.execute.get.getResponseBody should be ("foo")
  }

}
