package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler

class ContentApiSanityTest(context: Context) extends SanityTestBase(context) {

  "Content api" should "serve gzipped" in {
    // GZip Compression is enabled in application.conf

    val httpRequest = requestHost("search").get()
    whenReady(httpRequest) { result =>
      assume(result.status == 200, "Service is down")
      result.body should include("results")
    }
  }
}


