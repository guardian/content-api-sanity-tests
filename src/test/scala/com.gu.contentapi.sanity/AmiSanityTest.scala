package com.gu.contentapi.sanity


import org.scalatest.{Matchers, FlatSpec}
import com.gu.contentapi.sanity.HttpHandler

class AmiSanityTest extends FlatSpec with Matchers with HttpHandler {


  "www.theguardian.com" should "be on the latest version of the AMI" in {
    // this test looks on the page for the unique ID of the EU Ireland PV EBS-Backed 64-bit image
    // if this test fails you probably need to update the stack to the latest AMI (and update this test)

    val connection = GET(
      "http://aws.amazon.com/amazon-linux-ami/",
      compress = false
    )

    connection.body should include("ami-a921dfde")
  }
}
