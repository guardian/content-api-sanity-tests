package com.gu.contentapi.sanity.support

import org.scalatest.Suite
import org.scalatestplus.play.OneAppPerSuite
import play.api.GlobalSettings
import play.api.test.FakeApplication

trait FakeAppSupport extends OneAppPerSuite { self: Suite =>

  // override Global so that we don't start the Quartz scheduler
  override implicit lazy val app = FakeAppSupport.fakeApp

}

object FakeAppSupport {

  // override Global so that we don't start the Quartz scheduler
  val fakeApp = new FakeApplication(withGlobal = Some(new GlobalSettings {}))

}
