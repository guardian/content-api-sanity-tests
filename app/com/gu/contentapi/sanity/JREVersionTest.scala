package com.gu.contentapi.sanity

import org.scalatest.{Ignore, Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.TestFailedException

class JREVersionTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  {
    "The Content API" should "be using the latest JRE" taggedAs(LowPriorityTest) in {
      handleException {
      val httpRequest = request("http://java.com/applet/JreCurrentVersion2.txt").get
      whenReady(httpRequest) { result =>
     assume(result.status == 200, "Service is down")
      result.body should include("1.8.0_25")


      }
      }(fail, testNames.head, tags)
    }
  }
}
