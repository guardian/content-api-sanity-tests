package com.gu.contentapi.sanity


import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}

class AmiSanityTest extends FlatSpec with Matchers with ScalaFutures {

  "www.theguardian.com" should "be on the latest version of the AMI" in {
    // this test looks on the page for the unique ID of the EU Ireland PV EBS-Backed 64-bit image
    // if this test fails you probably need to update the stack to the latest AMI (and update this test)

    val httpRequest = request("http://aws.amazon.com/amazon-linux-ami/").withHeaders("Accept-Encoding" -> "").get()
    whenReady(httpRequest,timeout(Span(2, Seconds)),interval(Span(1, Second))) { result =>
      result.body should include ("ami-a921dfde")
        }
      }

}
