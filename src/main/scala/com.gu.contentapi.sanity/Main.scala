package com.gu.contentapi

import com.gu.contentapi.sanity._
import com.gu.sanity.utils.QuartzScheduler
import scala.concurrent.duration._

object StartScheduledRunner {
  def main(args: Array[String]) {
    onStart()
  }

  def onStart() {
    QuartzScheduler.start()
    QuartzScheduler schedule("Frequent Tests", runFreqeuntTests) every (30 seconds)
    QuartzScheduler schedule("Infrequent Tests", runInfrequentTests) at "0 0 12 1/1 * ? *"
    QuartzScheduler schedule("CODE environment Tests ", runCodeTests) at "0 0 12 ? * MON-FRI *"
  }

  def onStop {
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
    (new ReadComposerDraftInPreviewTest).execute
  }
}