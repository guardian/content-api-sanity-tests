package com.gu.contentapi.sanity

import org.scalatest.{Ignore, Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

@Ignore
class AmiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The Content API" should "be using the latest AMI" in {
    val httpRequest = request("https://cloud-images.ubuntu.com/locator/ec2/releasesTable").get
    whenReady(httpRequest) { result =>
      result.body should include("ami-896c96fe")
    }
  }
}
