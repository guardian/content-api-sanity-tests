package com.gu.contentapi

import utils.QuartzScheduler
import scala.concurrent.duration._
import org.scalatest.exceptions.TestFailedException

object StartScheduledRunner {
  def main(args: Array[String]) {
    println("Hello, world!")
    onStart()
  }

  def onStart() {
    QuartzScheduler.start()
    QuartzScheduler schedule("Frequent Tests", runFreqeuntTests) every (30 seconds)
    QuartzScheduler schedule("job 2 ", bar) at "0 0 3 * * ? *"
  }

  def onStop {
    QuartzScheduler.stop()
  }


  def runFreqeuntTests {
    (new AmiSanityTest).execute()
    }



  def bar {
    println("bar")
  }
}