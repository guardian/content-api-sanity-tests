package com.gu.contentapi.sanity

import java.net._
import io.Source
import java.util.zip.GZIPInputStream
import scala.Some




class Response(val connection: HttpURLConnection) {
  lazy val body = Source.fromInputStream(connection.getInputStream).getLines().mkString("")
  lazy val bodyFromGzip = Source.fromInputStream(new GZIPInputStream(connection.getInputStream)).getLines().mkString("")

  lazy val responseCode = connection.getResponseCode

  lazy val responseMessage = connection.getResponseMessage

  def header(name: String) = connection.getHeaderField(name)

  def disconnect() { connection.disconnect() }
}

trait HttpHandler {

  val proxy: Option[Proxy] = (Option(System.getProperty("http.proxyHost")), Option(System.getProperty("http.proxyPort"))) match {
    case (Some(host), Some(port)) => Some(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port.toInt)))
    case _ => None
  }

  def GET(url: String, compress: Boolean = false, headers: Seq[(String, String)] = Nil): Response = {

    val connection = proxy.map(p => new URL(url).openConnection(p)).getOrElse(new URL(url).openConnection()).asInstanceOf[HttpURLConnection]

    if (compress)
      connection.setRequestProperty("Accept-Encoding", "deflate,gzip")
    else
      connection.setRequestProperty("Accept-Encoding", "")

    headers.foreach{
      case (key, value) => connection.setRequestProperty(key, value)
    }
    new Response(connection)
  }
}


