package com.gu.contentapi.sanity

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import System._


class AmiSanityTest extends FlatSpec with ShouldMatchers with HttpHandler {

  import scala.concurrent.ExecutionContext.Implicits.global

  "www.theguardian.com" should "be on the latest version of the AMI" in {
    request("http://aws.amazon.com/amazon-linux-ami/").get() map { response =>
      response.body should include("ami-a921dfde")
    }
  }
}
