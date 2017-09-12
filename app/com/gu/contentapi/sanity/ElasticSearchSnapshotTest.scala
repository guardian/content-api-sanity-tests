package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.tags.ProdOnly
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import java.time.LocalDate

@ProdOnly
class ElasticSearchSnapshotTest(context: Context) extends SanityTestBase(context) {

  val s3Client: AmazonS3 = AmazonS3ClientBuilder.defaultClient()

    "Elasticsearch backup via snapshot" should "have been updated yesterday on S3" in {

      val yesterdayDate = LocalDate.now().minusDays(1L)
      // this test will break when we switch stacks. until a better solution, we will just amend when test breaks.
      val objectKey = s"PROD-ZEBRA/meta-${yesterdayDate}-00-00.dat"
      val snapshotAdded: Boolean = s3Client.doesObjectExist("content-api-es-snapshots", objectKey)

      snapshotAdded should be(true)

  }

}

