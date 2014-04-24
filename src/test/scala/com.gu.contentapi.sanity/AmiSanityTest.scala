package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class AmiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The Content API" should "be using the latest AMI" in {
    val httpRequest = request("https://cloud-images.ubuntu.com/locator/ec2/releasesTable").get
    teamCityNotifier("Check for the latest AMI", "AMI in test did not match latest AMI listed by Ubuntu") {
      whenReady(httpRequest) { result =>
        result.body should include ("ami-896c96fe")
      }
    }
  }

}
