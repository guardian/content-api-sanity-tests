package com.gu.contentapi.sanity

import org.scalatest.{Ignore, Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.TestFailedException

class JREVersionTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  {
    "The Content API" should "be using the latest JRE" taggedAs(InfrequentTest, PRODTest) in {
      handleException {
      val httpRequest = request("http://java.com/applet/JreCurrentVersion2.txt").get
      whenReady(httpRequest) { result =>
        if (result.status == 200) {
          result.body should include("1.7.0_65")
        }
        else {
          throw new TestFailedException("Java.com did not return a 200 status code", 1)
        }
      }
      }(fail, testNames.mkString, isLowPriority = true)
    }
  }
}
