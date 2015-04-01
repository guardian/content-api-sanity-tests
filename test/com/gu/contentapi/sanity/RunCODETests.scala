package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.{DoNothing, FakeAppSupport}
import play.api.test.Helpers.running

/**
 * Run all the tests that are suitable for execution against the CODE environment.
 *
 * Use `sbt test:run` to run this class.
 *
 */
object RunCODETests extends App {

  val suites = MetaSuites.codeStandalone(testFailureHandler = DoNothing)

  running(FakeAppSupport.fakeApp) {
    suites.foreach(_.execute)
  }

}
