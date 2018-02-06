package com.gu.contentapi.sanity.support

import java.io.File
import java.io.PrintWriter

trait XmlFileSupport {

  def createModifiedXMLTempFile(originalXML: String, originalString: String, replacedString: String): String = {
    val tempFile = File.createTempFile("TestIntegrationArticleModified-", ".xml")
    val modifiedArticleXML = originalXML.replaceAll(originalString, replacedString)
    val output = new PrintWriter(tempFile)
    output.write(modifiedArticleXML)
    output.close()
    tempFile.getAbsolutePath
  }

  def deleteFileIfExists(filePath: String): Unit = {
    val tempFile = new File(filePath)
    if (tempFile.exists) tempFile.delete()
    ()
  }

}
