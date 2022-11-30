package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.tags.ProdOnly
import org.slf4j.LoggerFactory

import java.time.LocalDate
import play.api.libs.ws.WSClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{HeadObjectRequest, NoSuchKeyException}

@ProdOnly
class ElasticSearchSnapshotTest(context: Context, wsClient: WSClient) extends SanityTestBase(context, wsClient) {
  private val logger = LoggerFactory.getLogger(getClass)
  val s3Client: S3Client = S3Client.builder().build()

    "Elasticsearch backup via snapshot" should "have been updated yesterday on S3" in {

      val yesterdayDate = LocalDate.now().minusDays(1L)
      // this test will break when we switch stacks. until a better solution, we will just amend when test breaks.
      val objectKey = s"PROD-ZEBRA/meta-${yesterdayDate}-00-00.dat"
      val req = HeadObjectRequest.builder().key(objectKey).bucket("content-api-es-snapshots").build()
      val snapshotSize: Long = try {
        val result = s3Client.headObject(req)
        result.contentLength()
      } catch {
        case _:NoSuchKeyException=>
          logger.warn(s"Nothing found for $objectKey in the backups bucket")
          0
        case other:Throwable=>
          throw other
      }

      snapshotSize should be > 0L
  }

}

