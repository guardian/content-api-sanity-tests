import com.gu.contentapi.sanity._
import com.gu.contentapi.sanity.support.{CloudWatchReporter, InfrequentScheduledTestsFailureHandler, FrequentScheduledTestFailureHandler}
import com.gu.contentapi.sanity.utils.QuartzScheduler
import org.joda.time.DateTime
import scala.concurrent.Future, scala.concurrent.duration._
import scala.language.postfixOps
import play.api._, play.api.inject.ApplicationLifecycle
import play.api.libs.ws.WSClient

class App(
  appLifecycle: ApplicationLifecycle,
  cloudWatchReporter: CloudWatchReporter,
  wsClient: WSClient) {

  def start = {
    Logger.info("Application has started")
    QuartzScheduler.start()
    QuartzScheduler schedule("Frequent Tests", runFrequentTests(cloudWatchReporter)) every (30 seconds)
    QuartzScheduler schedule("Infrequent Tests", runInfrequentTests(cloudWatchReporter)) at "0 0 9 ? * MON-FRI *"

    appLifecycle.addStopHook { () =>
      Logger.info("Application shutdown...")
      QuartzScheduler.stop()
      wsClient.close()
      Future.successful(())
    }
  }

  def runFrequentTests(cloudWatchReporter: CloudWatchReporter): Unit = {
    println(s"=== Starting frequent tests at ${new DateTime()} ===")
    val context = Context(testFailureHandler = FrequentScheduledTestFailureHandler, cloudWatchReporter = cloudWatchReporter)
    val suites = MetaSuites.prodFrequent(context, wsClient)
    suites.foreach(_.execute())
    println(s"=== Frequent tests finished at ${new DateTime()} ===")
    cloudWatchReporter.reportTestRunComplete()
  }

  def runInfrequentTests(cloudWatchReporter: CloudWatchReporter): Unit = {
    println(s"=== Starting infrequent tests at ${new DateTime()} ===")
    val context = Context(testFailureHandler = InfrequentScheduledTestsFailureHandler, cloudWatchReporter = cloudWatchReporter)
    val suites = MetaSuites.prodInfrequent(context, wsClient)
    suites.foreach(_.execute())
    println(s"=== Infrequent tests finished at ${new DateTime()} ===")
    cloudWatchReporter.reportTestRunComplete()
  }

}
