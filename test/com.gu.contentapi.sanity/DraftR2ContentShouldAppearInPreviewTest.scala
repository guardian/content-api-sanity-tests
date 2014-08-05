package com.gu.contentapi.sanity

import org.scalatest.{Ignore, Matchers, FlatSpec}
import org.scalatest.selenium.{WebBrowser}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.WebDriver
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience, Eventually}
import scala.io.Source
import org.scalatest.time.{Seconds, Span}


class DraftR2ContentShouldAppearInPreviewTest extends FlatSpec with Matchers with Eventually with IntegrationPatience with WebBrowser with ScalaFutures {

  val modifiedHeadline = "Content API Sanity Test " + java.util.UUID.randomUUID.toString
  val pageId = "435627291"
  implicit val webDriver: WebDriver = new HtmlUnitDriver

  "Updating a draft in R2" should "show an update in the Preview API" taggedAs(FrequentTest, CODETest) in {
    lazy val tempFilePathString = createModifiedXMLTempFile(Source.fromURL(getClass.getResource("/TestR2IntegrationArticle.xml")).mkString, "Facebook messaging article", modifiedHeadline)
    login(Config.r2AdminHost + "/tools/newspaperintegration/index")
    postR2ArticleToNewspaperIntegrationEndpoint(tempFilePathString)
    deleteFileIfExists(tempFilePathString)

    eventually(timeout(Span(60, Seconds))) {
      withClue(s"R2 Article with Page ID:$pageId did not show updated headline $modifiedHeadline within 60 seconds on item endpoint") {
        isCAPIShowingChange(Config.previewHostCode + "internal-code/content/" + pageId, modifiedHeadline, Some(Config.previewUsernameCode: String, Config.previewPasswordCode: String)) should be(true)
      }
      eventually(timeout(Span(60, Seconds))) {
        withClue(s"R2 Article with Page ID:$pageId did not show updated headline $modifiedHeadline within 60 seconds on search endpoint") {
          isCAPIShowingChange(Config.previewHostCode + "search?use-date=last-modified", modifiedHeadline, Some(Config.previewUsernameCode: String, Config.previewPasswordCode: String)) should be(true)
        }
      }
    }
  }

    def login(toolPath: String) {
      go to (toolPath)
      pageTitle should be("Login")
      textField("j_username").value = Config.r2AdminUsername
      pwdField("j_password").value = Config.r2AdminPassword
      submit
    }

    def postR2ArticleToNewspaperIntegrationEndpoint(r2XMLPath: String) {
      xpath("//form[@action='article/import']/input[@type='file']").webElement.sendKeys(r2XMLPath)
      click on xpath("//form[@action='article/import']/input[@type='submit']")
      //check import is successful
      val result = cssSelector("body").webElement.getText
      val items = result.split(":|;");
      val importStatus = items(0)
      val importedPageId = (items(1))
      importStatus should be("OK")
      importedPageId should be(pageId)
      close
    }

}
