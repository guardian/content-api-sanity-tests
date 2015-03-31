package com.gu.contentapi.sanity

class ContentApiSanityTest extends SanityTestBase {

  "Content api" should "serve gzipped" in {
    handleException{
      // GZip Compression is enabled in application.conf

      val httpRequest = requestHost("search").get()
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        result.body should include("results")
      }
    }(fail, testNames.head, tags)
  }
}


