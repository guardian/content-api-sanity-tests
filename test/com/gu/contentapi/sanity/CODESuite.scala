package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.{FakeAppSupport, DoNothingTestFailureHandler}
import org.scalatest.{TestSuite, Suites} 
import play.api.libs.ws.WSClient

/**
 * Nested suite of all the tests that should be run in the CODE environment
 */
class CODESuite(wsClient: WSClient) extends Suites(
  CODESuite.CodeOnlySuites ++
  MetaSuites.suitableForBothProdAndCode(context = Context.Testing, wsClient): _*) 
  with TestSuite

object CODESuite {

  /** Tests that should only be run on CODE, e.g. because they write data that would be undesirable in PROD */
  val CodeOnlySuites = Seq()

}