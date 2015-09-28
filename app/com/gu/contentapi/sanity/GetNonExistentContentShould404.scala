package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler

class GetNonExistentContentShould404(context: Context) extends SanityTestBase(context) {

    "GETting non existent content" should "404" in {
      val httpRequest = requestHost("foo/should-not-exist").get()
      whenReady(httpRequest) { result =>
        assumeNot(503, 504)(result)
        result.status should be(404)
      }
    }
}


