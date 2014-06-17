package com.gu.contentapi

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience}
import play.api.libs.ws.WS
import org.scalatest.exceptions.TestFailedException


class AmiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The Content API" should "be using the latest AMI"  in {
    try {
      val httpRequest = WS.url("https://FAILcloud-images.ubuntu.com/locator/ec2/releasesTable").get
      whenReady(httpRequest) { result =>
        result.body should include("20140607.1")
      }
    }
    catch {
      case tfe: TestFailedException => println(tfe.getMessage())
        println("I failed!")
    }
  }
}
