package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience}
import play.api.libs.ws.WS
import org.scalatest.exceptions.TestFailedException
import play.api.libs.json._




class AmiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The Content API" should "be using the latest AMI"  in {
      handleException {
        val httpRequest = WS.url("https://cloud-images.ubuntu.com/locator/ec2/releasesTable").get
        whenReady(httpRequest) { result =>
          result.body should include("FAIL")
        }
      }(fail,"The Content API should be using the latest AMI")
  }
}
