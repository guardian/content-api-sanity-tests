package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.tags.ProdOnly
import org.scalatest.Suite

object MetaSuites {

  def prodFrequent(context: Context) = Seq(
    new CanaryContentSanityTest(context),
    new SearchContainsLargeNumberOfResults(context),
    new PreviewRequiresAuthTest(context),
    new ContentApiSanityTest(context),
    new GetNonExistentContentShould404(context),
    new ContentSetFeaturesTest(context),
    new ValidateArticleSchema(context),
    new NewestItemFieldsTest(context),
    new MostViewedContainsItemsTest(context),
    new EditorsPicksContainsItemsTest(context),
    new CriticalTagsTest(context),
    new TagSearchContainsLargeNumberOfResults(context),
    new ShowBlocksTest(context)
  )

  def prodInfrequent(context: Context) = Seq(
    new JREVersionTest(context),
    new SSLExpiryTest(context),
    new CrosswordsIndexingTest(context)
  )

  /** All suites that can be run against either PROD or CODE */
  def suitableForBothProdAndCode(context: Context) =
    (prodFrequent(context) ++ prodInfrequent(context))
      .filter(isNotTaggedWithProdOnly)

  private def isNotTaggedWithProdOnly(s: Suite): Boolean = {
    s.getClass.getAnnotationsByType(classOf[ProdOnly]).isEmpty
  }

}
