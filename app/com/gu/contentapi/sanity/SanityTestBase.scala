package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support._
import org.scalatest.{Matchers, OptionValues, Retries, FlatSpec}
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience, Eventually}

abstract class SanityTestBase(context: Context)
  extends FlatSpec with Matchers with OptionValues
  with Eventually with Retries with ScalaFutures with IntegrationPatience
  with TestFailureHandlingSupport with CloudWatchReportingSupport
  with HttpRequestSupport with XmlFileSupport {

  val testFailureHandler = context.testFailureHandler
  val cloudWatchReporter = context.cloudWatchReporter
}



