package com.gu.contentapi

import java.io.File

import com.gu.contentapi.sanity._
import com.gu.sanity.utils.QuartzScheduler
import play.api.DefaultApplication
import play.core.StaticApplication
import play.core.server.NettyServer
import scala.concurrent.duration._

object StartScheduledRunner {
  def main(args: Array[String]) {
    onStart()
  }

  def onStart() {
    val server = new NettyServer(
      (new StaticApplication(new File("."))
      Option(System.getProperty("http.port")).fold(Option(9000))(p => if (p == "disabled") Option.empty[Int] else Option(Integer.parseInt(p))),
      Option(System.getProperty("https.port")).map(Integer.parseInt(_)),
      Option(System.getProperty("http.address")).getOrElse("0.0.0.0")
    )
    QuartzScheduler.start()
    QuartzScheduler schedule("Frequent Tests", runFreqeuntTests) every (30 seconds)
    QuartzScheduler schedule("Infrequent Tests", runInfrequentTests) at "0 0 12 ? * MON-FRI *"
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