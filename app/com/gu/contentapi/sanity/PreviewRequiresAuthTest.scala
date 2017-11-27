package com.gu.contentapi.sanity

import org.scalatest.time.{Seconds, Span}
import play.api.libs.ws.WSClient

class PreviewRequiresAuthTest(context: Context, wsClient: WSClient) extends SanityTestBase(context, wsClient) {

  "GETting preview content" should "require authentication" in {
    //Sometimes this throws a java.io.IOException, with message: Remotely Closed, so using Eventually to retry
    eventually(timeout(Span(21, Seconds)),interval(Span(3, Seconds))) {
      val httpRequest = request(Config.previewHost).get()
      whenReady(httpRequest) { result =>
        assumeNotInsideEventually(503, 504)(result)
        result.status should be(401)

      }
    }
  }
}
