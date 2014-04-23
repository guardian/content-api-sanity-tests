package com.gu.contentapi.sanity


import org.scalatest.{Failed, Matchers, FlatSpec}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.exceptions.TestFailedException

class AmiSanityTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "www.theguardian.com" should "be on the latest version of the AMI" in {
    println("##teamcity[message text='AMI Test Failed' errorDetails='Check amazon for the latest AMI' status='WARNING']")
    // this test looks on the page for the unique ID of the EU Ireland PV EBS-Backed 64-bit image
    // if this test fails you probably need to update the stack to the latest AMI (and update this test)
    try{
      val httpRequest = request("http://aws.amazon.com/amazon-linux-ami/").get

      whenReady(httpRequest) { result =>
      result.body should include ("ami-FAIL")
      }
    } catch {
      case e: TestFailedException =>  {
        println("##teamcity[message text='AMI Test Failed' errorDetails='Check amazon for the latest AMI' status='WARNING']")
        throw e
      }
    }
   }


}
