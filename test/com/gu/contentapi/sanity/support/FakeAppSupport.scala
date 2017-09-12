package com.gu.contentapi.sanity.support

import org.scalatest.{TestData, TestSuite}
import org.scalatestplus.play.{OneAppPerTest, OneAppPerSuite, FakeApplicationFactory}

trait FakeAppSupport extends OneAppPerSuite 
  with FakeApplicationFactory { self: TestSuite =>

  override implicit lazy val app = fakeApplication()

}

