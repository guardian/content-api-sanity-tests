package com.gu.contentapi.sanity

import org.scalatest.tagobjects.Retryable
import org.scalatest.time.{Minute, Span}
import org.scalatest.{Retries, Matchers, FlatSpec}
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}

class PreviewRequiresAuthTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience with Retries with Eventually {

  "GETting preview content" should "require authentication" taggedAs(FrequentTest, PRODTest) in {

    handleException {
      val httpRequest = request(Config.previewHost).get
      //Sometimes this throws a java.io.IOException, with message: Remotely Closed, so using Eventually to retry
        eventually {
          whenReady(httpRequest) { result =>
            result.status should be(401)
          }
      }
    }(fail,testNames.head, tags)
  }
}
