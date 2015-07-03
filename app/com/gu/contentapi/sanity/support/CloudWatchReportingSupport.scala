package com.gu.contentapi.sanity.support

import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.regions.{ServiceAbbreviations, Regions, Region}
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient
import com.amazonaws.services.cloudwatch.model.{MetricDatum, PutMetricDataRequest, Dimension}
import play.api.{Logger, Configuration}

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
    def cfg(key: String) = config.getString(s"content-api-sanity-tests.cloudwatch-$key")
    val reporter = for {
      namespace <- cfg("namespace")
      stack <- cfg("stack")
      testRunsMetric <- cfg("test-runs-metric")
      successfulTestsMetric <- cfg("successful-tests-metric")
      failedTestsMetric <- cfg("failed-tests-metric")
    } yield {
      Logger.info(s"Will report metrics to CloudWatch. namespace=$namespace, stack=$stack, metrics=($testRunsMetric, $successfulTestsMetric, $failedTestsMetric)")
      new RealCloudWatchReporter(namespace, stack, testRunsMetric, successfulTestsMetric, failedTestsMetric)
    }

    reporter getOrElse {
      Logger.info("Will not report any metrics to CloudWatch")
      DoNothingCloudWatchReporter
    }
  }

}

class RealCloudWatchReporter(namespace: String,
                              stack: String,
                              testRunsMetric: String,
                              successfulTestsMetric: String,
                              failedTestsMetric: String) extends CloudWatchReporter {

  private val region = Region.getRegion(Regions.EU_WEST_1)

  lazy val stackDimension = new Dimension().withName("Stack").withValue(stack)

  lazy val cloudwatch = {
    val client = new AmazonCloudWatchAsyncClient()
    client.setEndpoint(region.getServiceEndpoint(ServiceAbbreviations.CloudWatch))
    client
  }

  object LoggingAsyncHandler extends AsyncHandler[PutMetricDataRequest, Void] {
    def onError(exception: Exception) {
      Logger.info(s"CloudWatch PutMetricDataRequest error: ${exception.getMessage}}")
    }
    def onSuccess(request: PutMetricDataRequest, result: Void) {
      Logger.trace("CloudWatch PutMetricDataRequest - success")
    }
  }

  private def put(metricName: String, value: Double): Unit = {
    val metric = new MetricDatum()
      .withValue(value)
      .withMetricName(metricName)
      .withDimensions(stackDimension)

    val request = new PutMetricDataRequest().withNamespace(namespace).withMetricData(metric)

    cloudwatch.putMetricDataAsync(request, LoggingAsyncHandler)
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