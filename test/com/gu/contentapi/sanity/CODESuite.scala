package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.{FakeAppSupport, DoNothing}
import org.scalatest.Suites

/**
 * Nested suite of all the tests that should be run in the CODE environment
 */
class CODESuite extends Suites(MetaSuites.codeStandalone(testFailureHandler = DoNothing): _*) with FakeAppSupport
