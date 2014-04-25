package com.gu.contentapi

import play.api.libs.ws.WS
import org.scalatest.exceptions.TestFailedException

package object sanity {

  def request(uri: String) = WS.url(uri).withRequestTimeout(7500)

  def teamCityNotifier(testName: String,testErrorMessage: String)(test: => Unit){
    try{
      test
    } catch {
      case e: TestFailedException =>  {
        println(s"##teamcity[testFailed name='$testName' message='$testErrorMessage']")
        println(s"##teamcity[buildStatus status='FAILURE' text='{build.status.text} $testName | $testErrorMessage']")
        throw e
      }
    }
  }

  def requestHost(path: String) =
  // make sure query string is included
    if(path.contains("?"))
    request(Config.host + path + "&api-key=" + Config.apiKey)
    else
    request(Config.host + path + "?&api-key=" + Config.apiKey)

  def retryNTimes(numberOfAttempts: Int, attemptInterval: Int)(test: => Boolean): Boolean = {
    if (numberOfAttempts == 0)
      false
    else if (test)
      true
    else {
      println(s"attempt $numberOfAttempts")
      Thread.sleep(attemptInterval)
      retryNTimes(numberOfAttempts - 1, attemptInterval)(test)
    }
  }

}
