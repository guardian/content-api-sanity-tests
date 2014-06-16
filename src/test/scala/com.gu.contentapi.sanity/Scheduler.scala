package com.gu.contentapi.sanity

import scala.concurrent.duration._
import play.api.Application
import play.api.GlobalSettings

import utils.QuartzScheduler

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    QuartzScheduler.start()
    QuartzScheduler schedule("job 1 ", foo) every (5 seconds)
    QuartzScheduler schedule("job 2 ", bar) at "0 0 3 * * ? *"
  }

  override def onStop(app: Application) {
    QuartzScheduler.stop()
  }


  def foo {
    println("foo")
  }

  def bar {
    println("bar")
  }

}
