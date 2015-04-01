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

  val fakeApp = new FakeApplication(

    // override Global so that we don't start the Quartz scheduler
    withGlobal = Some(new GlobalSettings {}),

    // don't trip up on the Guardian self-signed certificate in CODE
    additionalConfiguration = Map("ws.acceptAnyCertificate" -> true))

}
