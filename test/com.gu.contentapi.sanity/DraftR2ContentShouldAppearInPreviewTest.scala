package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.FakeAppSupport
import org.scalatest.tagobjects.Retryable
import org.scalatest.selenium.WebBrowser
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.WebDriver
import scala.io.Source
import org.scalatest.time.{Minutes, Seconds, Span}


class DraftR2ContentShouldAppearInPreviewTest extends SanityTestBase with WebBrowser with FakeAppSupport {

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetryOnFailure (Span(1, Minutes))(super.withFixture(test))
    else
      super.withFixture(test)
  }

  val modifiedHeadline = "Content API Sanity Test " + java.util.UUID.randomUUID.toString
  implicit val webDriver: WebDriver = new HtmlUnitDriver

  "Updating a draft in R2" should "show an update in the Preview API" taggedAs Retryable in {
    lazy val tempFilePathString = createModifiedXMLTempFile(Source.fromURL(getClass.getResource("/TestR2IntegrationArticle.xml")).mkString, "Facebook messaging article", modifiedHeadline)
    login(Config.r2AdminHost + "/tools/newspaperintegration/index")
    val pageId = postR2ArticleToNewspaperIntegrationEndpoint(tempFilePathString)
    deleteFileIfExists(tempFilePathString)

    eventually(timeout(Span(60, Seconds))) {
      withClue(s"R2 Article with Page ID:$pageId did not show updated headline $modifiedHeadline within 60 seconds on item endpoint") {
        isCAPIShowingChange(Config.previewHost + "internal-code/content/" + pageId, modifiedHeadline, Some(Config.previewUsernameCode, Config.previewPasswordCode)) should be(true)
      }
      eventually(timeout(Span(60, Seconds))) {
        withClue(s"R2 Article with Page ID:$pageId did not show updated headline $modifiedHeadline within 60 seconds on search endpoint") {
          isCAPIShowingChange(Config.previewHost + "search?use-date=last-modified&page-size=100", modifiedHeadline, Some(Config.previewUsernameCode, Config.previewPasswordCode)) should be(true)
        }
      }
    }
  }

  def login(toolPath: String) {
    go to toolPath
    pageTitle should be("Login")
    textField("j_username").value = Config.r2AdminUsername
    pwdField("j_password").value = Config.r2AdminPassword
    submit
  }

  /** POSTs an article to the R2 integration endpoint.
    *
    * @return page ID of created article
    */

  def postR2ArticleToNewspaperIntegrationEndpoint(r2XMLPath: String): String = {
    assume (!(pageTitle equals "Service Unavailable"), "R2 is down")
    xpath("//form[@action='article/import']/input[@type='file']").webElement.sendKeys(r2XMLPath)
    click on xpath("//form[@action='article/import']/input[@type='submit']")
    //check import is successful
    val result = cssSelector("body").webElement.getText
    val items = result.split(":|;")
    val importStatus = items(0)
    val importedPageId = items(1)
    importStatus should be("OK")
    close
    importedPageId
  }

}
