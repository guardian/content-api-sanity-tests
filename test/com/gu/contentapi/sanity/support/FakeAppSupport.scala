package com.gu.contentapi.sanity.support

import org.scalatest.{TestData, TestSuite}
import org.scalatestplus.play.{OneAppPerTest, GuiceOneAppPerSuite, FakeApplicationFactory}

trait FakeAppSupport extends GuiceOneAppPerSuite 
  with FakeApplicationFactory { self: TestSuite =>

  override implicit lazy val app = fakeApplication()

}

