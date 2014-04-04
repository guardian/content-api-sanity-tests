package com.gu.contentapi.sanity

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.ws.WS

class CanaryWritingSanityTest extends FlatSpec with Matchers with ScalaFutures {

  import scala.concurrent.ExecutionContext.Implicits.global

  "Making a PUT" should "return a 200 response code" in {
    request("http://httpbin.org/put").put("hello world") map { response =>
      response.status should equal(200)
    }
  }

}