package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.{ CloudWatchReporter, FrequentScheduledTestFailureHandler, InfrequentScheduledTestsFailureHandler }
import com.gu.contentapi.sanity.utils.QuartzScheduler
import org.joda.time.DateTime
import play.api._, play.api.inject.ApplicationLifecycle
import play.api.mvc.{ ControllerComponents, BaseController }
import scala.concurrent.Future
import scala.concurrent.duration._

class Application(
  override val controllerComponents: ControllerComponents,
  lifecycle: ApplicationLifecycle,
  cloudWatchReporter: CloudWatchReporter
) extends BaseController {

  lifecycle.addStopHook { () =>
    Logger.info("Application shutdown...")
    Future.successful(QuartzScheduler.stop())
  }

  Logger.info("Application has started")
  QuartzScheduler.start()
  QuartzScheduler schedule("Frequent Tests", runFrequentTests(cloudWatchReporter)) every (30.seconds)
  QuartzScheduler schedule("Infrequent Tests", runInfrequentTests(cloudWatchReporter)) at "0 0 9 ? * MON-FRI *"

  def runFrequentTests(cloudWatchReporter: CloudWatchReporter): Unit = {
    println(s"=== Starting frequent tests at ${new DateTime()} ===")
    val context = Context(testFailureHandler = FrequentScheduledTestFailureHandler, cloudWatchReporter = cloudWatchReporter)
    val suites = MetaSuites.prodFrequent(context)
    suites.foreach(_.execute())
    println(s"=== Frequent tests finished at ${new DateTime()} ===")
    cloudWatchReporter.reportTestRunComplete()
  }

  def runInfrequentTests(cloudWatchReporter: CloudWatchReporter): Unit = {
    println(s"=== Starting infrequent tests at ${new DateTime()} ===")
    val context = Context(testFailureHandler = InfrequentScheduledTestsFailureHandler, cloudWatchReporter = cloudWatchReporter)
    val suites = MetaSuites.prodInfrequent(context)
    suites.foreach(_.execute())
    println(s"=== Infrequent tests finished at ${new DateTime()} ===")
    cloudWatchReporter.reportTestRunComplete()
  }

}