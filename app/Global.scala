import com.gu.contentapi.sanity._
import com.gu.contentapi.sanity.utils.QuartzScheduler
import scala.concurrent.duration._
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    QuartzScheduler.start()
    QuartzScheduler schedule("Frequent Tests", runFrequentTests) every (30 seconds)
    QuartzScheduler schedule("Infrequent Tests", runInfrequentTests) at "0 0 12 ? * MON-FRI *"
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    QuartzScheduler.stop()
  }

  def runFrequentTests {
    (new CanaryWritingSanityTest).execute
    (new SearchContainsLargeNumberOfResults).execute
    (new PreviewRequiresAuthTest).execute
    (new ContentApiSanityTest).execute
    (new GetNonExistentContentShould404).execute
    (new PreviewContentSetNotInLiveTest).execute
  }



  def runInfrequentTests {
    (new JREVersionTest).execute
    (new AmiSanityTest).execute
  }

}
