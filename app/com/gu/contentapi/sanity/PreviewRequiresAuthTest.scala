package com.gu.contentapi.sanity

import org.scalatest.time.{Seconds, Span}

class PreviewRequiresAuthTest extends SanityTestBase {

  "GETting preview content" should "require authentication" in {

    handleException {
      //Sometimes this throws a java.io.IOException, with message: Remotely Closed, so using Eventually to retry
      eventually(timeout(Span(15, Seconds)),interval(Span(3, Seconds))) {
        val httpRequest = request(Config.previewHost).get()
        whenReady(httpRequest) { result =>
          assume(result.status !=503,"Service is down")
          result.status should be(401)

        }
      }
    }(fail,testNames.head, tags)
  }
}
