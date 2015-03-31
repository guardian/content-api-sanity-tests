package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.{XmlFileSupport, TestFailureHandlingSupport, HttpRequestSupport}
import org.scalatest.{Retries, FlatSpec}
import org.scalatest.concurrent.Eventually

trait SanityTestBase
  extends FlatSpec with Eventually with Retries
  with HttpRequestSupport with TestFailureHandlingSupport with XmlFileSupport


