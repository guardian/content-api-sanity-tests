package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.selenium.{WebBrowser}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.WebDriver
import org.scalatest.concurrent.{IntegrationPatience, Eventually}
import org.openqa.selenium.firefox.FirefoxDriver
import scala.io.Source

class DraftR2ContentShouldAppearInPreviewTest extends FlatSpec with Matchers with Eventually with IntegrationPatience with WebBrowser {

  implicit val webDriver: WebDriver = new FirefoxDriver

  "With crendentials I" should "be able to create a draft article in R2" in {
    val assetPath = getClass.getResource("/TestR2IntegrationArticle.xml").toString
    go to (Config.r2AdminHost + "/tools/newspaperintegration/index")
    pageTitle should be ("Login")
    textField("j_username").value = Config.r2AdminUsername
    pwdField("j_password").value = Config.r2AdminPassword
    submit
    xpath("//form[@action='article/import']/input[@type='file']").webElement.sendKeys(assetPath)
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
  }
}
