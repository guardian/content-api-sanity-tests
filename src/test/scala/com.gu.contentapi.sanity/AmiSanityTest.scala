package com.gu.contentapi.sanity


import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class AmiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "www.theguardian.com" should "be on the latest version of the AMI" in {
    // this test looks on the page for the unique ID of the EU Ireland PV EBS-Backed 64-bit image
    // if this test fails you probably need to update the stack to the latest AMI (and update this test)

    val httpRequest = request("http://aws.amazon.com/amazon-linux-ami/").get

    teamCityNotifier("Check amazon for the latest AMI","AMI in test did not match latest AMI on Amazon website") {
      whenReady(httpRequest) { result =>
        result.body should include ("ami-2918e35e")
      }
    }
  }
}
