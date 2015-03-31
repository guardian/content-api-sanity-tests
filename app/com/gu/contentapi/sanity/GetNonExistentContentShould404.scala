package com.gu.contentapi.sanity

class GetNonExistentContentShould404 extends SanityTestBase {

    "GETting non existent content" should "404" in {
      handleException {
        val httpRequest = requestHost("foo/should-not-exist").get()
        whenReady(httpRequest) { result =>
          assume(result.status != 503)
          result.status should be(404)
        }
      }(fail,testNames.head, tags)
    }
}


