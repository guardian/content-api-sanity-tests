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

  def retryNTimes(n: Int, attemptInterval: Int)(test: => Boolean): Boolean = {
    if (n == 0)
      false
    else if (test)
      true
    else {
      println(s"attempt $n")
      Thread.sleep(attemptInterval)
      retryNTimes(n - 1, attemptInterval)(test)
    }
  }

}
