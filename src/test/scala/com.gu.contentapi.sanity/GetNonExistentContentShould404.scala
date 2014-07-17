package com.gu.contentapi.sanity2

import org.scalatest.{Matchers, FlatSpec, matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class GetNonExistentContentShould404 extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

    "GETting non existent content" should "404" taggedAs(FrequentTest, PRODTest) in {

       val httpRequest = requestHost("foo/should-not-exist").get
        whenReady(httpRequest) { result =>
            result.status should be (404)
        }
    }
}


