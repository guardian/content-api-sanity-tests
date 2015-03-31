package com.gu.contentapi.sanity.support

import java.io.File

import scalax.file.Path
import scalax.io.{Resource, Output}

trait XmlFileSupport {

  def createModifiedXMLTempFile(originalXML: String, originalString: String, replacedString: String): String = {
    val tempFile = File.createTempFile("TestIntegrationArticleModified-", ".xml")
    val modifiedArticleXML = originalXML.replaceAll(originalString, replacedString)
    val output: Output = Resource.fromFile(tempFile)
    output.write(modifiedArticleXML)
    tempFile.getAbsolutePath
  }

  def deleteFileIfExists(filePath: String): Unit = {
    val tempFilePath: Path = Path.fromString(filePath)
    tempFilePath.deleteIfExists()
  }

}
