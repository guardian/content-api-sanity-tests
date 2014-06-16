package com.gu.contentapi

import utils.QuartzScheduler
import scala.concurrent.duration._

object StartScheduledRunner {
  def main(args: Array[String]) {
    println("Hello, world!")
    onStart()
  }

  def onStart() {
    QuartzScheduler.start()
    QuartzScheduler schedule("job 1 ", foo) every (5 seconds)
    QuartzScheduler schedule("job 2 ", bar) at "0 0 3 * * ? *"
  }

  def onStop {
    QuartzScheduler.stop()
  }


  def foo {
    println("foo")
    (new AmiSanityTest).execute()
  }

  def bar {
    println("bar")
  }
}