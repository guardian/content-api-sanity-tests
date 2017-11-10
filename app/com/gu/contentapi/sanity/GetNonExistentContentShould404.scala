package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import play.api.libs.ws.WSClient

class GetNonExistentContentShould404(context: Context, wsClient: WSClient) extends SanityTestBase(context, wsClient) {

    "GETting non existent content" should "404" in {
      val httpRequest = requestHost("foo/should-not-exist").get()
      whenReady(httpRequest) { result =>
        assumeNot(503, 504)(result)
        result.status should be(404)
      }
    }
}


