package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support._
import org.scalatest.{OptionValues, Retries}
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.ws.WSClient

abstract class SanityTestBase(context: Context, override val wsClient: WSClient)
  extends AnyFlatSpec with Matchers with OptionValues
  with Eventually with Retries with ScalaFutures with IntegrationPatience
  with TestFailureHandlingSupport with CloudWatchReportingSupport
  with HttpRequestSupport with XmlFileSupport {

  val testFailureHandler = context.testFailureHandler
  val cloudWatchReporter = context.cloudWatchReporter

}



