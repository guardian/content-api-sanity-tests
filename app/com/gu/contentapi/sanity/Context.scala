package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.{DoNothingCloudWatchReporter, DoNothingTestFailureHandler, CloudWatchReporter, TestFailureHandler}

case class Context(testFailureHandler: TestFailureHandler,
                   cloudWatchReporter: CloudWatchReporter)

object Context {

  /**
   * The context to be used when running `sbt test`.
   * Does not send any PagerDuty alerts or report any metrics to CloudWatch.
   */
  val Testing = Context(testFailureHandler = DoNothingTestFailureHandler,
                        cloudWatchReporter = DoNothingCloudWatchReporter)

}


