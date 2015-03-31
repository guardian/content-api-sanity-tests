import com.gu.contentapi.sanity._
import com.gu.contentapi.sanity.utils.QuartzScheduler
import org.joda.time.DateTime
import scala.concurrent.duration._
import scala.language.postfixOps
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    QuartzScheduler.start()
    QuartzScheduler schedule("Frequent Tests", runFrequentTests()) every (30 seconds)
    QuartzScheduler schedule("Infrequent Tests", runInfrequentTests()) at "0 0 9 ? * MON-FRI *"
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    QuartzScheduler.stop()
  }

  def runFrequentTests(): Unit = {
    println(s"=== Starting frequent tests at ${new DateTime()} ===")
    (new CanaryWritingSanityTest).execute
    (new SearchContainsLargeNumberOfResults).execute
    (new PreviewRequiresAuthTest).execute
    (new ContentApiSanityTest).execute
    (new GetNonExistentContentShould404).execute
    (new PreviewContentSetNotInLiveTest).execute
    (new ValidateArticleSchema).execute
    (new NewestItemFieldsTest).execute
    (new MostViewedContainsItemsTest).execute
    (new CriticalTagsTest).execute
    (new TagSearchContainsLargeNumberOfResults).execute
    println(s"=== Frequent tests finished at ${new DateTime()} ===")
  }



  def runInfrequentTests(): Unit = {
    println(s"=== Starting infrequent tests at ${new DateTime()} ===")
    (new JREVersionTest).execute
    (new AmiSanityTest).execute
    (new SSLExpiryTest).execute
    (new CrosswordsIndexingTest).execute
    println(s"=== Infrequent tests finished at ${new DateTime()} ===")
  }

}
