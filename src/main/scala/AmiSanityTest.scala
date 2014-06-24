package com.gu.contentapi

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience}
import play.api.libs.ws.WS
import org.scalatest.exceptions.TestFailedException
import play.api.libs.json._




class AmiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "The Content API" should "be using the latest AMI"  in {
    try {
      val httpRequest = WS.url("https://cloud-images.ubuntu.com/locator/ec2/releasesTable").get
      whenReady(httpRequest) { result =>
        result.body should include("20140607.1")
      }
    }
    catch {
      case tfe: TestFailedException =>

        println("starting custom exception handling!")
        val description =  tfe.getMessage()
        val data = Json.obj(
        "service_key" -> Config.pagerDutyServiceKey,
        "event_type" -> "trigger",
        "description" -> description,
        "client" -> "Content API Sanity Tests",
        "client_url" -> "https://github.com/guardian/content-api-sanity-tests"
      )
        val httpRequest = WS.url("https://events.pagerduty.com/generic/2010-04-15/create_event.json").post(data)
        whenReady(httpRequest) { result =>
        result.body should include("success")}
        fail
    }
  }
}
