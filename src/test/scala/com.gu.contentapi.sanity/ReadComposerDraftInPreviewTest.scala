package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import scala.io.Source
import java.io.File
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import com.ning.http.multipart.FilePart
import com.ning.http.multipart.MultipartRequestEntity
import com.ning.http.multipart.Part


class ReadComposerDraftInPreviewTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Posting an article into Composer" should "respond with OK" in {
    val httpRequest = request(Config.composerHost + "/incopyintegration/index").withHeaders("User-Agent" -> "curl").get
    whenReady(httpRequest) { result => result.body should equal("incopy Integration...")
    }

      val bos = new ByteArrayOutputStream
    val filename = getClass.getResource("/composer_article.xml").toURI
    filename should equal ("foo")
    println(filename)
      // Build up the Multiparts
      val parts: Array[Part] = Array(new FilePart("file", new File(filename)))

      // Add it to the MultipartRequestEntity
      val reqE = new MultipartRequestEntity(parts, null)
      reqE.writeRequest(bos)
      val reqIS = new ByteArrayInputStream(bos.toByteArray)

      val data:Array[Byte] = Array()
      reqIS.read(data)

      val postArticle = request(Config.composerHost + "incopyintegration/article/import").withHeaders("User-Agent" -> "curl").post(data)
      whenReady(postArticle) { result =>
        result.body should equal(200 )
      }
    }

}
