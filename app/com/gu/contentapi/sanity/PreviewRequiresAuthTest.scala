package com.gu.contentapi.sanity

import org.scalatest.tagobjects.Retryable
import org.scalatest.time.{Minute, Span}
import org.scalatest.{Retries, Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class PreviewRequiresAuthTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience with Retries {

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetryOnFailure (Span(1, Minute))(super.withFixture(test))
    else
      super.withFixture(test)
  }

  "GETting preview content" should "require authentication" taggedAs(FrequentTest, PRODTest, Retryable) in {

    handleException {
      val httpRequest = request(Config.previewHost).get

      whenReady(httpRequest) { result =>
        result.status should be(401)
      }
    }(fail,testNames.head, tags)
  }
}
