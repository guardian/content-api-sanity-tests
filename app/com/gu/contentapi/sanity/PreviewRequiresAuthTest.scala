package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import org.scalatest.time.{Seconds, Span}

class PreviewRequiresAuthTest(context: Context) extends SanityTestBase(context) {

  "GETting preview content" should "require authentication" in {
    //Sometimes this throws a java.io.IOException, with message: Remotely Closed, so using Eventually to retry
    eventually(timeout(Span(15, Seconds)),interval(Span(3, Seconds))) {
      val httpRequest = request(Config.previewHost).get()
      whenReady(httpRequest) { result =>
        assumeNot5xxResponse(result)
        result.status should be(401)

      }
    }
  }
}
