package com.gu.contentapi.sanity

import org.scalatest.{FlatSpec}
import javax.net.ssl._
import java.net.{ProtocolException, MalformedURLException, URL}
import org.joda.time.{Days, DateTime}
import java.io.IOException
import sun.security.x509.X509CertImpl

class SSLExpiryTest extends FlatSpec {

  "SSL Certificates" should "be more than 30 days from expiry" taggedAs(LowPriorityTest) in {
    handleException {
      val hosts = Seq (
        Config.host,
        Config.hostPublicSecure,
        Config.previewHost,
        Config.writeHost,
        Config.writePreviewHost
      )

      val secureHosts = for (host <- hosts) yield host.replaceAll("http://", "https://")

      for (host <- secureHosts) {
        try {
          val url = new URL(host)
          val conn = url.openConnection().asInstanceOf[HttpsURLConnection]
          conn.setHostnameVerifier(new HostnameVerifier {
            override def verify(hostnameVerifier: String, sslSession: SSLSession) = true
          }
          )
          conn.connect()
          val certs = conn.getServerCertificates
          certs.headOption.map { cert =>
            val x = cert.asInstanceOf[X509CertImpl]
            val expiry = x.getNotAfter.getTime
            val daysleft = Days.daysBetween(new DateTime(), new DateTime(expiry.toLong)).getDays
            if (daysleft < 30) {
              fail("Cert for %s expires in %d days".format(host, daysleft))
            }
          }
          conn.disconnect()
        } catch {
          case e: MalformedURLException => {
            fail("Malformed url exception attempting to check ssl cert for %s, exception: %s".format(host, e.getMessage))
          }
          case e: ProtocolException => {
            fail("Protocol exception attempting to check ssl cert for %s, exception: %s".format(host, e.getMessage))
          }
          case e: IllegalStateException => {
            fail("IllegalStateException exception attempting to check ssl cert for %s, exception: %s".format(host, e.getMessage))
          }
          case e: NullPointerException => {
            fail("NullPointerException attempting to check ssl cert for %s, exception: %s".format(host, e.getMessage))
          }
          case e: IOException => {
            fail("IOexception attempting to check ssl cert for %s, exception: %s".format(host, e.getMessage))
          }
        }
      }
    }  (fail,testNames.head, tags)
  }
}
