package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import com.gu.contentapi.sanity.tags.ProdOnly
import org.scalatest.Suite

object MetaSuites {

  def prodFrequent(testFailureHandler: TestFailureHandler) = Seq(
    new CanaryWritingSanityTest(testFailureHandler),
    new SearchContainsLargeNumberOfResults(testFailureHandler),
    new PreviewRequiresAuthTest(testFailureHandler),
    new ContentApiSanityTest(testFailureHandler),
    new GetNonExistentContentShould404(testFailureHandler),
    new PreviewContentSetNotInLiveTest(testFailureHandler),
    new ValidateArticleSchema(testFailureHandler),
    new NewestItemFieldsTest(testFailureHandler),
    new MostViewedContainsItemsTest(testFailureHandler),
    new CriticalTagsTest(testFailureHandler),
    new TagSearchContainsLargeNumberOfResults(testFailureHandler)
  )

  def prodInfrequent(testFailureHandler: TestFailureHandler) = Seq(
    new JREVersionTest(testFailureHandler),
    new AmiSanityTest(testFailureHandler),
    new SSLExpiryTest(testFailureHandler),
    new CrosswordsIndexingTest(testFailureHandler)
  )

  def codeStandalone(testFailureHandler: TestFailureHandler) =
    (prodFrequent(testFailureHandler) ++ prodInfrequent(testFailureHandler))
      .filter(isNotTaggedWithProdOnly)

  private def isNotTaggedWithProdOnly(s: Suite): Boolean = {
    s.getClass.getAnnotationsByType(classOf[ProdOnly]).isEmpty
  }

}