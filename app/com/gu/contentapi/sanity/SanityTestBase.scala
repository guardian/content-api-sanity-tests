package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.{TestFailureHandler, XmlFileSupport, TestFailureHandlingSupport, HttpRequestSupport}
import org.scalatest.{Matchers, OptionValues, Retries, FlatSpec}
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience, Eventually}

abstract class SanityTestBase(val testFailureHandler: TestFailureHandler)
  extends FlatSpec with Matchers with OptionValues
  with Eventually with Retries with ScalaFutures with IntegrationPatience
  with HttpRequestSupport with TestFailureHandlingSupport with XmlFileSupport



