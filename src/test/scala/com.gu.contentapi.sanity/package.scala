package com.gu.contentapi

import play.api.libs.ws.WS
import org.scalatest.exceptions.TestFailedException

package object sanity {

  def request(uri: String) = WS.url(uri).withRequestTimeout(7500)

  def requestHost(path: String) =
  // make sure query string is included
    if(path.contains("?"))
    request(Config.host + path + "&api-key=" + Config.apiKey)
    else
    request(Config.host + path + "?api-key=" + Config.apiKey)

  def retryNTimes(numberOfAttempts: Int, attemptInterval: Int)(test: => Boolean): Boolean = {
    if (numberOfAttempts == 0)
      false
    else if (test)
      true
    else {
      println(s"attempts remaining $numberOfAttempts")
      Thread.sleep(attemptInterval)
      retryNTimes(numberOfAttempts - 1, attemptInterval)(test)
    }
  }

}
