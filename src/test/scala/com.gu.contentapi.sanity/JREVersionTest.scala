package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class JREVersionTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {
  {
    "The Content API" should "be using the latest JRE" in {
      val httpRequest = request("http://java.com/applet/JreCurrentVersion2.txt").get
      teamCityNotifier("Check for the latest JRE", "JRE in test did not match latest JRE listed by java.com") {
        whenReady(httpRequest) { result =>
          result.body should include ("1.7.0_55")
        }
      }
    }
  }
}
