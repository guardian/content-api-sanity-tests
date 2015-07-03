package com.gu.contentapi.sanity.support

import play.api.Configuration

trait CloudWatchReportingSupport {

  def cloudWatchReporter: CloudWatchReporter

}

trait CloudWatchReporter {

  def reportSuccessfulTest(): Unit
  def reportFailedTest(): Unit
  def reportTestRunComplete(): Unit

}

object CloudWatchReporter {

  def apply(config: Configuration): CloudWatchReporter = {
    // TODO
    DoNothingCloudWatchReporter
  }

}

object DoNothingCloudWatchReporter extends CloudWatchReporter {
  override def reportSuccessfulTest(): Unit = {}
  override def reportFailedTest(): Unit = {}
  override def reportTestRunComplete(): Unit = {}
}