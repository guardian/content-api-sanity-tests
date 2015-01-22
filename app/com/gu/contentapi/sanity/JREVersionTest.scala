package com.gu.contentapi.sanity

import org.scalatest.time.{Span, Seconds}
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class JREVersionTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  {
    "The Content API" should "be using the latest JRE" taggedAs(LowPriorityTest) in {
      handleException {
      val httpRequest = request("http://java.com/applet/JreCurrentVersion2.txt").get
      assume(httpRequest.isReadyWithin(Span(5, Seconds)))
      whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      result.body should include("1.8.0_31")


      }
      }(fail, testNames.head, tags)
    }
  }
}
