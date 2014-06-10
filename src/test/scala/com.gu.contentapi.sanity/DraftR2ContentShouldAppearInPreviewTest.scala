package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.selenium.{WebBrowser}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.WebDriver
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience, Eventually}
import com.ning.http.client.Realm.AuthScheme
import scala.io.Source
import scalax.io._
import scalax.file.Path
import org.scalatest.time.{Seconds, Span}

class DraftR2ContentShouldAppearInPreviewTest extends FlatSpec with Matchers with Eventually with IntegrationPatience with WebBrowser with ScalaFutures {

  val modifiedHeadline = "Content API Sanity Test " + java.util.UUID.randomUUID.toString
  val pageId = "435627291"
  val tempFile = java.io.File.createTempFile("TestR2IntegrationArticleModified-", ".xml")
  val tempFilePathString = tempFile.toString
  val tempFilePath: Path = Path(tempFile)

  implicit val webDriver: WebDriver = new HtmlUnitDriver

  "Updating a draft in R2" should "show an update in the Preview API" taggedAs(FrequentTest) in {

    createModifiedR2Article(Source.fromURL(getClass.getResource("/TestR2IntegrationArticle.xml")).mkString, tempFilePathString)
    login(Config.r2AdminHost + "/tools/newspaperintegration/index")
    postR2ArticleToNewspaperIntegrationEndpoint(tempFilePathString)
    cleanup

    eventually(timeout(Span(60, Seconds))) {
      withClue(s"R2 Article with Page ID:$pageId did not show updated headline $modifiedHeadline within 60 seconds on item endpoint") {
        doesCAPIHaveModifiedDraftData("internal-code/content/"+pageId) should be(true)
      }
      eventually(timeout(Span(60, Seconds))) {
        withClue(s"R2 Article with Page ID:$pageId did not show updated headline $modifiedHeadline within 60 seconds on search endpoint") {
          doesCAPIHaveModifiedDraftData("search?use-date=last-modified") should be(true)
        }
      }
    }

    def createModifiedR2Article(originalR2XML: String, modifiedR2XMLPath: String) {
      val r2ArticleXML = originalR2XML
      val modifiedR2ArticleXML = r2ArticleXML.replace("Facebook messaging article", modifiedHeadline)
      val output: Output = Resource.fromFile(tempFilePathString)
      output.write(modifiedR2ArticleXML)

    }

    def login(toolPath: String) {
      go to (toolPath)
      pageTitle should be("Login")
      textField("j_username").value = Config.r2AdminUsername
      pwdField("j_password").value = Config.r2AdminPassword
      submit
    }

    def postR2ArticleToNewspaperIntegrationEndpoint(r2XMLPath: String)
    {
      xpath("//form[@action='article/import']/input[@type='file']").webElement.sendKeys(r2XMLPath)
      click on xpath("//form[@action='article/import']/input[@type='submit']")
      //check import is successful
      val result = cssSelector("body").webElement.getText
      val items = result.split(":|;");
      val importStatus = items(0)
      val importedPageId = (items(1))
      importStatus should be("OK")
      importedPageId should be (pageId)
      close
    }

    def cleanup {
      tempFilePath.deleteIfExists()
    }

    def doesCAPIHaveModifiedDraftData(capiURI: String) = {
      val httpRequest = request(Config.previewHostCode + capiURI).withAuth(Config.previewUsernameCode, Config.previewPasswordCode, AuthScheme.BASIC).get
      whenReady(httpRequest) { result => result.body.contains(modifiedHeadline)}
    }
  }
}