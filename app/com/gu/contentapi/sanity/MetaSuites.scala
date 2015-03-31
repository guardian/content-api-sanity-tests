package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.tags.ProdOnly
import org.scalatest.Suite

object MetaSuites {

  val ProdFrequent = Seq(
    new CanaryWritingSanityTest,
    new SearchContainsLargeNumberOfResults,
    new PreviewRequiresAuthTest,
    new ContentApiSanityTest,
    new GetNonExistentContentShould404,
    new PreviewContentSetNotInLiveTest,
    new ValidateArticleSchema,
    new NewestItemFieldsTest,
    new MostViewedContainsItemsTest,
    new CriticalTagsTest,
    new TagSearchContainsLargeNumberOfResults
  )

  val ProdInfrequent = Seq(
    new JREVersionTest,
    new AmiSanityTest,
    new SSLExpiryTest,
    new CrosswordsIndexingTest
  )

  val CodeStandalone = (ProdFrequent ++ ProdInfrequent)
      .filter(isNotTaggedWithProdOnly)

  private def isNotTaggedWithProdOnly(s: Suite): Boolean = {
    s.getClass.getAnnotationsByType(classOf[ProdOnly]).isEmpty
  }

}
