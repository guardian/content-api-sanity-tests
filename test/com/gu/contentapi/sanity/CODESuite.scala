package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.{FakeAppSupport, DoNothingTestFailureHandler}
import org.scalatest.Suites

/**
 * Nested suite of all the tests that should be run in the CODE environment
 */
class CODESuite extends Suites(
  CODESuite.CodeOnlySuites ++
  MetaSuites.suitableForBothProdAndCode(context = Context.Testing): _*) with FakeAppSupport

object CODESuite {

  /** Tests that should only be run on CODE, e.g. because they write data that would be undesirable in PROD */
  val CodeOnlySuites = Seq(
    new DraftR2ContentShouldAppearInPreviewTest,
    new ReadComposerDraftInPreviewTest
  )

}