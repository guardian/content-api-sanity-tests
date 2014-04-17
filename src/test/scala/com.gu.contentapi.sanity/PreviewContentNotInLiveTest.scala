package com.gu.contentapi.sanity

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Second, Span}
import org.joda.time.format.{ISODateTimeFormat}


class PreviewContentNotInLiveTest extends FlatSpec with Matchers with ScalaFutures {
  "GETting the preview content set" should "show no results on live" in {
      val httpRequest = request(Config.host + "collections/canary").get()
      whenReady(httpRequest) { result =>
        result.body should include ("foo")
    }
  }
}
