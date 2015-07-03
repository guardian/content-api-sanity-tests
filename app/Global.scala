import com.gu.contentapi.sanity._
import com.gu.contentapi.sanity.support.{CloudWatchReporter, InfrequentScheduledTestsFailureHandler, FrequentScheduledTestFailureHandler}
import com.gu.contentapi.sanity.utils.QuartzScheduler
import org.joda.time.DateTime
import scala.concurrent.duration._
import scala.language.postfixOps
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    val cloudWatchReporter = CloudWatchReporter(app.configuration)

    Logger.info("Application has started")
    QuartzScheduler.start()
    QuartzScheduler schedule("Frequent Tests", runFrequentTests(cloudWatchReporter)) every (30 seconds)
    QuartzScheduler schedule("Infrequent Tests", runInfrequentTests(cloudWatchReporter)) at "0 0 9 ? * MON-FRI *"
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    QuartzScheduler.stop()
  }

  def runFrequentTests(cloudWatchReporter: CloudWatchReporter): Unit = {
    println(s"=== Starting frequent tests at ${new DateTime()} ===")
    val context = Context(testFailureHandler = FrequentScheduledTestFailureHandler, cloudWatchReporter = cloudWatchReporter)
    val suites = MetaSuites.prodFrequent(context)
    suites.foreach(_.execute)
    println(s"=== Frequent tests finished at ${new DateTime()} ===")
  }

  def runInfrequentTests(cloudWatchReporter: CloudWatchReporter): Unit = {
    println(s"=== Starting infrequent tests at ${new DateTime()} ===")
    val context = Context(testFailureHandler = InfrequentScheduledTestsFailureHandler, cloudWatchReporter = cloudWatchReporter)
    val suites = MetaSuites.prodInfrequent(context)
    suites.foreach(_.execute)
    println(s"=== Infrequent tests finished at ${new DateTime()} ===")
  }

}
