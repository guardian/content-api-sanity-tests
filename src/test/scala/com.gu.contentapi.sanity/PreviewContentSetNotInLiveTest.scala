package com.gu.contentapi.sanity

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures


class PreviewContentSetNotInLiveTest extends FlatSpec with Matchers with ScalaFutures {
  "GETting the preview content set" should "show no results on live" in {
      val httpRequest = request(Config.host + "search?content-set=preview").get()
      whenReady(httpRequest) { result =>
        result.body should include (""""total":0""")
        result.body should include (""""results":[]""")
    }
  }
}
