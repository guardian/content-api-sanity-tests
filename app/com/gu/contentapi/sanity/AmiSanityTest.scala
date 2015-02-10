package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience}
import play.api.libs.ws.WS
import play.api.Play.current
import org.scalatest.time.{Span, Seconds}


class AmiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The Content API" should "be using the latest AMI" taggedAs(LowPriorityTest)  in {
    val currentAMI= "20140927"

    handleException {
        val httpRequest = WS.url("https://cloud-images.ubuntu.com/locator/ec2/releasesTable").get
        assume(httpRequest.isReadyWithin(Span(5, Seconds)),"Service took too long to respond")
        whenReady(httpRequest) { result =>
          assume(result.status == 200, "Service is down")
          withClue (s"EC2 release table did not include $currentAMI, check https://cloud-images.ubuntu.com/locator/ec2/ for latest AMI.") {
            result.body should include (currentAMI)
          }
        }
      }(fail,testNames.head, tags)
  }
}
