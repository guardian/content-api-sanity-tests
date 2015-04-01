package com.gu.contentapi.sanity.support

import org.scalatest.{TestData, Suite}
import org.scalatestplus.play.{OneAppPerTest, OneAppPerSuite}
import play.api.GlobalSettings
import play.api.test.FakeApplication

trait FakeAppSupport extends OneAppPerSuite { self: Suite =>

  override implicit lazy val app = new FakeApplication(

    // override Global so that we don't start the Quartz scheduler
    withGlobal = Some(new GlobalSettings {}),

    // don't trip up on the Guardian self-signed certificate in CODE
    additionalConfiguration = Map("ws.acceptAnyCertificate" -> true))

}

