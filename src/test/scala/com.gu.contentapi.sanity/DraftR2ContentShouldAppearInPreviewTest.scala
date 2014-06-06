package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.selenium.{WebBrowser}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.WebDriver
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience, Eventually}
import com.ning.http.client.Realm.AuthScheme
import scala.io.Source
import scalax.io._
import org.openqa.selenium.firefox.FirefoxDriver
import scalax.file.Path

class DraftR2ContentShouldAppearInPreviewTest extends FlatSpec with Matchers with Eventually with IntegrationPatience with WebBrowser with ScalaFutures {

  implicit val webDriver: WebDriver = new FirefoxDriver

  "Updating a draft in R2" should "show an update in the Preview API" in {
    val tempFilePath = "/tmp/TestR2IntegrationArticleModified.xml"
    val path: Path = Path.fromString(tempFilePath)
    path.deleteIfExists()
    val r2ArticleXML = Source.fromURL(getClass.getResource("/TestR2IntegrationArticle.xml")).getLines.mkString
    val modifiedR2ArticleXML = r2ArticleXML.replace("Facebook messaging article", "Content API Sanity Test "/*+java.util.UUID.randomUUID.toString*/)
    val output:Output = Resource.fromFile(tempFilePath)
    output.write(r2ArticleXML)
    go to (Config.r2AdminHost + "/tools/newspaperintegration/index")
    pageTitle should be ("Login")
    textField("j_username").value = Config.r2AdminUsername
    pwdField("j_password").value = Config.r2AdminPassword
    submit
    xpath("//form[@action='article/import']/input[@type='file']").webElement.sendKeys("/tmp/TestR2IntegrationArticleModified.xml")
    click on xpath("//form[@action='article/import']/input[@type='submit']")
    //check import is successful
    val result = cssSelector("body").webElement.getText
    result should include ("OK")
    val items = result.split(":|;");
    val importStatus = items(0)
    val pageId = items(1)
    importStatus should be ("OK")
    pageId should be ("435627291")
    close

    val httpRequest = request(Config.previewHostCode+"internal-code/content/435627291").withAuth(Config.previewUsernameCode, Config.previewPasswordCode, AuthScheme.BASIC).get
    whenReady(httpRequest) { result =>
      result.body should include ("Facebook messaging article")
    }
  }
}
