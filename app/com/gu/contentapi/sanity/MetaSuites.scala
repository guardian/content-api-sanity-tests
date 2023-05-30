package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.tags.ProdOnly
import org.scalatest.Suite
import play.api.libs.ws.WSClient

object MetaSuites {

  def prodFrequent(context: Context, wsClient: WSClient) = Seq(
    new SearchContainsLargeNumberOfResults(context, wsClient),
    new PreviewRequiresAuthTest(context, wsClient),
    new ContentApiSanityTest(context, wsClient),
    new GetNonExistentContentShould404(context, wsClient),
    new ValidateArticleSchema(context, wsClient),
    new NewestItemFieldsTest(context, wsClient),
    new MostViewedContainsItemsTest(context, wsClient),
    new EditorsPicksContainsItemsTest(context, wsClient),
    new CriticalTagsTest(context, wsClient),
    new TagSearchContainsLargeNumberOfResults(context, wsClient),
    new ShowBlocksTest(context, wsClient)
  )

  def prodInfrequent(context: Context, wsClient: WSClient) = Seq(
    //new JREVersionTest(context), disabled for now because it spams us every day
    new SSLExpiryTest(context, wsClient),
    new CrosswordsIndexingTest(context, wsClient)
  )

  /** All suites that can be run against either PROD or CODE */
  def suitableForBothProdAndCode(context: Context, wsClient: WSClient) =
    (prodFrequent(context, wsClient) ++ prodInfrequent(context, wsClient))
      .filter(isNotTaggedWithProdOnly)

  private def isNotTaggedWithProdOnly(s: Suite): Boolean = {
    s.getClass.getAnnotationsByType(classOf[ProdOnly]).isEmpty
  }

}
