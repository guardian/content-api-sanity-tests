package com.gu.contentapi.sanity.support

import org.scalatest.Suite
import org.scalatestplus.play.OneAppPerSuite
import play.api.GlobalSettings
import play.api.test.FakeApplication

trait FakeAppSupport extends OneAppPerSuite { self: Suite =>

  // override Global so that we don't start the Quartz scheduler
  override implicit lazy val app = new FakeApplication(withGlobal = Some(new GlobalSettings {}))

}
