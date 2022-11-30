package com.gu.contentapi.sanity.support

import com.gu.contentapi.sanity.Config
import org.scalatest.exceptions.TestPendingException
import org.scalatest.Assertions
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.ws.{WSClient, WSResponse, WSAuthScheme, WSRequest}
import scala.concurrent.duration._

trait HttpRequestSupport extends ScalaFutures with Matchers with Assertions {

  def wsClient: WSClient

  def request(uri: String): WSRequest = wsClient.url(uri).withRequestTimeout(10000.millis)

  def requestHost(path: String) =
    // make sure query string is included
    if (path.contains("?"))
      request(Config.host + path + "&api-key=" + Config.apiKey)
    else
      request(Config.host + path + "?api-key=" + Config.apiKey)

  def isCAPIShowingChange(capiURI: String, modifiedString: String, credentials: Option[(String, String)] = None) = {
    val httpRequest = credentials match {
      case Some((username, password)) =>
        request(capiURI).withAuth(Config.previewUsernameCode, Config.previewPasswordCode, WSAuthScheme.BASIC)
      case None =>
        request(capiURI)
    }
    whenReady(httpRequest.get()) { result =>
      withClue("Authentication failed, check credentials") {
        result.status should not equal 401
      }
      result.body.contains(modifiedString)
    }
  }

  import HttpRequestSupport._

  def assumeNot(statuses:Int*)(response: WSResponse): Unit = {
    statuses.foreach(s => assume(response.status != s, statusMsg.getOrElse(s, "")))
  }

  def assumeNotInsideEventually(statuses:Int*)(response: WSResponse): Unit = {
    // workaround until https://github.com/scalatest/scalatest/pull/807 is merged
    statuses.foreach(s => if (response.status == s) throw new TestPendingException)
  }

}


object HttpRequestSupport {
  val statusMsg = Map(503 -> "Service is currently not available", 504 -> "ELB timeout")
}