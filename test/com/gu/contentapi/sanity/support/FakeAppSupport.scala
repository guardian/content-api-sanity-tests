package com.gu.contentapi.sanity.support

import org.scalatest.{TestData, TestSuite}
import org.scalatestplus.play.FakeApplicationFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.ws.ahc.AhcWSClient

trait FakeAppSupport extends GuiceOneAppPerSuite with FakeApplicationFactory { self: TestSuite =>

  override implicit lazy val app = fakeApplication()
  
}

