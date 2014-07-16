package com.gu.contentapi

import org.scalatest.exceptions.TestFailedException
import play.api.libs.ws.WS

package object sanity {




  def request(uri: String) = WS.url(uri).withRequestTimeout(10000)

  def handleException(test: =>Unit)(fail: =>Unit, testName: String)= {
  try {
    test
  } catch {
    case tfe: TestFailedException =>
      println(testName)
      pagerDutyAlerter(testName, tfe)
      fail
  }
  }
def pagerDutyAlerter(testName: String, tfe: TestFailedException){
       val description = testName + tfe.getMessage()
  //        val data = Json.obj(
  //        "service_key" -> Config.pagerDutyServiceKey,
  //        "event_type" -> "trigger",
  //        "description" -> description,
  //        "client" -> "Content API Sanity Tests",
  //        "client_url" -> "https://github.com/guardian/content-api-sanity-tests"
  //      )
  //        val httpRequest = WS.url("https://events.pagerduty.com/generic/2010-04-15/create_event.json").post(data)
  //        whenReady(httpRequest) { result =>
  //        result.body should include("success")}

}
  def requestHost(path: String) =
  // make sure query string is included
    if(path.contains("?"))
    request(Config.host + path + "&api-key=" + Config.apiKey)
    else
    request(Config.host + path + "?api-key=" + Config.apiKey)
}
