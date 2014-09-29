package com.gu.contentapi.sanity

import org.scalatest.tagobjects.Retryable
import org.scalatest.time.{Minutes, Seconds, Minute, Span}
import org.scalatest.{Retries, Matchers, FlatSpec}
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}

class PreviewRequiresAuthTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience with Retries with Eventually {

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetryOnFailure (Span(30, Seconds))(super.withFixture(test))
    else
      super.withFixture(test)
  }

  "GETting preview content" should "require authentication" taggedAs(FrequentTest, PRODTest, Retryable) in {

    handleException {
      //Sometimes this throws a java.io.IOException, with message: Remotely Closed, so using Eventually to retry
      eventually(timeout(Span(2, Minutes)),interval(Span(10, Seconds))) {
        var httpRequest = request(Config.previewHost).get
          whenReady(httpRequest) { result =>
            assume(result.status !=503,"Service is down")
            result.status should be(401)

          }
      }
    }(fail,testNames.head, tags)
  }
}
