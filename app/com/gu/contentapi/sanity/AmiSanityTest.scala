package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import com.gu.contentapi.sanity.tags.LowPriorityTest
import org.scalatest.time.{Span, Seconds}

class AmiSanityTest(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

  "The Content API" should "be using the latest AMI" taggedAs LowPriorityTest in {
    val currentAMI = "20140927"
    val httpRequest = request("https://cloud-images.ubuntu.com/locator/ec2/releasesTable").get()
    assume(httpRequest.isReadyWithin(Span(5, Seconds)),"Service took too long to respond")
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      withClue (s"EC2 release table did not include $currentAMI, check https://cloud-images.ubuntu.com/locator/ec2/ for latest AMI.") {
        result.body should include (currentAMI)
      }
    }
  }
}
