package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import com.gu.contentapi.sanity.tags.{ProdOnly, LowPriorityTest}
import org.scalatest.time.{Span, Seconds}

@ProdOnly
class JREVersionTest(context: Context) extends SanityTestBase(context) {

  {
    "The Content API" should "be using the latest JRE" taggedAs LowPriorityTest in {
      val httpRequest = request("http://java.com/applet/JreCurrentVersion2.txt").get()
      assume(httpRequest.isReadyWithin(Span(5, Seconds)))
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        result.body should include("1.8.0_45")

        // TODO we should add an endpoint to Concierge that reports the JVM version,
        // so we can check whether it matches the value we just got.
      }
    }
  }
}
