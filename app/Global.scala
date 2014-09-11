import com.gu.contentapi.sanity._
import com.gu.contentapi.sanity.utils.QuartzScheduler
import scala.concurrent.duration._
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    QuartzScheduler.start()
    QuartzScheduler schedule("Frequent Tests", runFreqeuntTests) every (30 seconds)
    QuartzScheduler schedule("Infrequent Tests", runInfrequentTests) at "0 0 12 ? * MON-FRI *"
    QuartzScheduler schedule("CODE environment Tests ", runCodeTests) at "0 0 12 ? * MON-FRI *"
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    QuartzScheduler.stop()
  }

  def runFreqeuntTests {
    (new CanaryWritingSanityTest).execute
    (new PreviewRequiresAuthTest).execute
    (new ContentApiSanityTest).execute
    (new GetNonExistentContentShould404).execute
    (new PreviewContentSetNotInLiveTest).execute
  }



  def runInfrequentTests {
    (new JREVersionTest).execute
    (new AmiSanityTest).execute
  }

  def runCodeTests {
    (new DraftR2ContentShouldAppearInPreviewTest).execute
    // This fails on EC2 as it is restricted to internal IP addresses
    // (new ReadComposerDraftInPreviewTest).execute
  }

}
