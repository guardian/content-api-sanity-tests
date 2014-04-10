package test

import org.scalatest.{Matchers, FlatSpec}
import com.gu.contentapi.sanity.HttpHandler

class AmiSanityTest extends FlatSpec with Matchers with HttpHandler {


  "www.theguardian.com" should "be on the latest version of the AMI" in {

    // if this test fails you probably need to update the stack to the latest AMI (and update this test)

    val connection = GET(
      "http://aws.amazon.com/amazon-linux-ami/",
      compress = false
    )

    connection.body should include("ami-a921dfde")
  }
}
