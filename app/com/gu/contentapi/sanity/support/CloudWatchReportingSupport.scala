package com.gu.contentapi.sanity.support

import org.scalatest.{Failed, Outcome, Succeeded, TestSuite}
import org.slf4j.LoggerFactory
import play.api.{Configuration, Logger}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.model.{MetricDatum, PutMetricDataRequest, PutMetricDataResponse}
import software.amazon.awssdk.services.cloudwatch.{CloudWatchAsyncClient, CloudWatchClient, CloudWatchClientBuilder}

import java.util.concurrent.CompletableFuture
import scala.concurrent.ExecutionContext.Implicits.global
import scala.compat.java8.FutureConverters
import scala.util.{Failure, Success, Try}

trait CloudWatchReportingSupport extends TestSuite {

  def cloudWatchReporter: CloudWatchReporter

  override protected def withFixture(test: NoArgTest): Outcome = {
    val outcome = super.withFixture(test)
    outcome match {
      case Failed(e) => cloudWatchReporter.reportFailedTest()
      case Succeeded => cloudWatchReporter.reportSuccessfulTest()
      case _ => // do nothing
    }
    outcome
  }

}

trait CloudWatchReporter {

  def reportSuccessfulTest(): Unit
  def reportFailedTest(): Unit
  def reportTestRunComplete(): Unit

}

object CloudWatchReporter {
  private val logger = LoggerFactory.getLogger(getClass)

  def apply(config: Configuration): CloudWatchReporter = {
    def cfg(key: String) = config.getOptional[String](s"content-api-sanity-tests.cloudwatch-$key")
    val reporter = for {
      namespace <- cfg("namespace")
      testRunsMetric <- cfg("test-runs-metric")
      successfulTestsMetric <- cfg("successful-tests-metric")
      failedTestsMetric <- cfg("failed-tests-metric")
    } yield {
      logger.info(s"Will report metrics to CloudWatch. namespace=$namespace, metrics=($testRunsMetric, $successfulTestsMetric, $failedTestsMetric)")
      new RealCloudWatchReporter(namespace, testRunsMetric, successfulTestsMetric, failedTestsMetric)
    }

    reporter getOrElse {
      logger.info("Will not report any metrics to CloudWatch")
      DoNothingCloudWatchReporter
    }
  }

}

class RealCloudWatchReporter(namespace: String,
                             testRunsMetric: String,
                             successfulTestsMetric: String,
                             failedTestsMetric: String) extends CloudWatchReporter {

  private val logger = LoggerFactory.getLogger(getClass)

  lazy val cloudwatch = CloudWatchAsyncClient.builder().region(Region.EU_WEST_1).build()

  private def loggingAsyncHandler[A](input:Try[A]) = input match {
    case Success(_)=>
      logger.info("CloudWatch PutMetricData request - success")
    case Failure(exception)=>
      logger.warn(s"CloudWatch PutMetricDataRequest error: ${exception.getMessage}}")
  }

  /**
   * Convenience method to keep the old behaviour.
   * Takes a function which returns a Java style future, and wraps this into a Scala future with some basic
   * success/failure logging on it.
   * @param f function to run. This must return some kind of CompletableFuture
   */
  private def loggedAsyncCall[T](f: ()=>CompletableFuture[T]) = {
    FutureConverters.toScala(f()).onComplete(loggingAsyncHandler)
  }

  private def put(metricName: String, value: Double): Unit = {
    val metric = MetricDatum.builder()
      .value(value)
      .metricName(metricName)
      .build()

    val request = PutMetricDataRequest.builder()
      .namespace(namespace)
      .metricData(metric)
      .build()

    loggedAsyncCall { ()=>
      cloudwatch.putMetricData(request)
    }
  }

  override def reportSuccessfulTest(): Unit = put(successfulTestsMetric, 1)

  override def reportFailedTest(): Unit = put(failedTestsMetric, 1)

  override def reportTestRunComplete(): Unit = put(testRunsMetric, 1)

}

object DoNothingCloudWatchReporter extends CloudWatchReporter {
  override def reportSuccessfulTest(): Unit = {}
  override def reportFailedTest(): Unit = {}
  override def reportTestRunComplete(): Unit = {}
}