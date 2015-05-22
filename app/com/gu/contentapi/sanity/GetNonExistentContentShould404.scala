package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler

class GetNonExistentContentShould404(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

    "GETting non existent content" should "404" in {
      val httpRequest = requestHost("foo/should-not-exist").get()
      whenReady(httpRequest) { result =>
        assumeNot5xxResponse(result)
        result.status should be(404)
      }
    }
}


