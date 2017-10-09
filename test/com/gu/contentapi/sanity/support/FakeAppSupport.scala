package com.gu.contentapi.sanity.support

import org.scalatest.{TestData, TestSuite}
import org.scalatestplus.play.FakeApplicationFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

trait FakeAppSupport extends GuiceOneAppPerSuite 
  with FakeApplicationFactory { self: TestSuite =>

  override implicit lazy val app = fakeApplication()

}

