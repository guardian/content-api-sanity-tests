import com.gu.contentapi.sanity._
import com.gu.contentapi.sanity.support.{CloudWatchReporter, FrequentScheduledTestFailureHandler, InfrequentScheduledTestsFailureHandler}
import com.gu.contentapi.sanity.utils.QuartzScheduler
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import play.api._
import play.api.inject.ApplicationLifecycle
import play.api.libs.ws.WSClient

class App(
  appLifecycle: ApplicationLifecycle,
  cloudWatchReporter: CloudWatchReporter,
  wsClient: WSClient) {

  val logger = LoggerFactory.getLogger(getClass)

  def start = {
    logger.info("Application has started")
    QuartzScheduler.start()
    QuartzScheduler schedule("Frequent Tests", runFrequentTests(cloudWatchReporter)) every (30 seconds)
    QuartzScheduler schedule("Infrequent Tests", runInfrequentTests(cloudWatchReporter)) at "0 0 9 ? * MON-FRI *"

    appLifecycle.addStopHook { () =>
      logger.info("Application shutdown...")
      QuartzScheduler.stop()
      wsClient.close()
      Future.successful(())
    }
  }

  def runFrequentTests(cloudWatchReporter: CloudWatchReporter): Unit = {
    logger.info(s"=== Starting frequent tests at ${new DateTime()} ===")
    val context = Context(testFailureHandler = new FrequentScheduledTestFailureHandler(wsClient), cloudWatchReporter = cloudWatchReporter)
    val suites = MetaSuites.prodFrequent(context, wsClient)
    suites.foreach(_.execute())
    logger.info(s"=== Frequent tests finished at ${new DateTime()} ===")
    cloudWatchReporter.reportTestRunComplete()
  }

  def runInfrequentTests(cloudWatchReporter: CloudWatchReporter): Unit = {
    logger.info(s"=== Starting infrequent tests at ${new DateTime()} ===")
    val context = Context(testFailureHandler = new InfrequentScheduledTestsFailureHandler(wsClient), cloudWatchReporter = cloudWatchReporter)
    val suites = MetaSuites.prodInfrequent(context, wsClient)
    suites.foreach(_.execute())
    logger.info(s"=== Infrequent tests finished at ${new DateTime()} ===")
    cloudWatchReporter.reportTestRunComplete()
  }

}
