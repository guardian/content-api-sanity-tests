package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.selenium.{HtmlUnit, WebBrowser}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.WebDriver
import org.scalatest.concurrent.{IntegrationPatience, Eventually}

class DraftR2ContentShouldAppearInPreviewTest extends FlatSpec with Matchers with Eventually with IntegrationPatience with WebBrowser {

  implicit val webDriver: WebDriver = new HtmlUnitDriver

  "With crendentials I" should "be able to create a draft article in R2" in {
    go to (Config.r2AdminHost + "/tools/article/new")
    pageTitle should be ("Login")
    textField("j_username").value = Config.r2AdminUsername
    pwdField("j_password").value = Config.r2AdminPassword
    submit
    pageTitle should be ("Article: new")
    textField("tagPickerInput").value = "Football"
    click on "tag-picker-add"
    textField("content.headline").value = "Content API Sanity Test Draft "+java.util.UUID.randomUUID.toString
    textField("content.page.linkText").value = "Content API Sanity Test Draft "+java.util.UUID.randomUUID.toString
    textArea("bodyText").value = "Content API Sanity Test Draft "+java.util.UUID.randomUUID.toString
    submit
    eventually{
      pageTitle should include ("Sanity Test")
    }
  }
}
