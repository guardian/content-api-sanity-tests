package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.FakeAppSupport
import play.api.test.Helpers.running

/**
 * Run all the tests that are suitable for execution against the CODE environment.
 *
 * Use `sbt test:run` to run this class.
 *
 */
object RunCODETests extends App {

  running(FakeAppSupport.fakeApp) {
    MetaSuites.CodeStandalone.foreach(_.execute)
  }

}
