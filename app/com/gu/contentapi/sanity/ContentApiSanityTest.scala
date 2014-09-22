package com.gu.contentapi.sanity

import org.scalatest.tagobjects.Retryable
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Retries, Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class ContentApiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience with Retries {

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetryOnFailure (Span(30, Seconds))(super.withFixture(test))
    else
      super.withFixture(test)
  }

  "Content api" should "serve gzipped" taggedAs(FrequentTest, Retryable) in {
    handleException{
      // GZip Compression is enabled in application.conf

      val httpRequest = requestHost("search?format=json&api-key=").get
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        result.body should include("results")
      }
    }(fail, testNames.head, tags)
  }
}


