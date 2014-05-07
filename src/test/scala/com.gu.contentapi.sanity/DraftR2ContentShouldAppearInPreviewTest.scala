package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.selenium.{HtmlUnit, WebBrowser}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.WebDriver
import org.scalatest.concurrent.Eventually

class DraftR2ContentShouldAppearInPreviewTest extends FlatSpec with Matchers with Eventually with WebBrowser {

  implicit val webDriver: WebDriver = new HtmlUnitDriver

  "With crendentials I" should "be able to login to R2" in {
    go to (Config.r2AdminHost)
    pageTitle should be ("Login")
    click on "j_username"
    textField("j_username").value = Config.r2AdminUsername
    pwdField("j_password").value = Config.r2AdminPassword
    submit
    eventually { pageTitle should be
      "Ed index home" }
  }
}
