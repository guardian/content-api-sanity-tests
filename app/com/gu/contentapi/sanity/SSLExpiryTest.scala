package com.gu.contentapi.sanity

import com.gu.contentapi.sanity.support.TestFailureHandler
import com.gu.contentapi.sanity.tags.{ProdOnly, LowPriorityTest}

import scala.util.{Success, Failure}
import javax.net.ssl._
import java.net.URL
import org.joda.time.{Days, DateTime}
import sun.security.x509.X509CertImpl

import scala.util.Try

@ProdOnly
class SSLExpiryTest(testFailureHandler: TestFailureHandler) extends SanityTestBase(testFailureHandler) {

  "SSL Certificates" should "be more than 30 days from expiry" taggedAs LowPriorityTest in {
    val hosts = Seq(
      Config.host,
      Config.hostPublicSecure,
      Config.previewHost,
      Config.writeHost,
      Config.writePreviewHost
    )

    val secureHosts = hosts map {
      _.replaceAll("http://", "https://")
    }

    for (host <- secureHosts) {

      val url = new URL(host)
      val conn = url.openConnection().asInstanceOf[HttpsURLConnection]
      conn.setHostnameVerifier(new HostnameVerifier {
        override def verify(hostnameVerifier: String, sslSession: SSLSession) = true
      })

      val certsTry = Try {
        conn.connect()
        conn.getServerCertificates
      }
      conn.disconnect()

      certsTry match {
        case Success(certs) =>
          withClue(s"No Certificates found for $host") {
            certs.length should be >= 1
          }
          certs.headOption.foreach { cert =>
            cert shouldBe a[X509CertImpl]
            val x = cert.asInstanceOf[X509CertImpl]
            val expiry = x.getNotAfter.getTime
            val daysleft = Days.daysBetween(new DateTime(), new DateTime(expiry.toLong)).getDays
            if (daysleft < 30) {
              fail("Cert for %s expires in %d days".format(host, daysleft))
            }
          }
        case Failure(e) =>
          cancel(s"Cancelling test as exception thrown: ${e.getClass.getSimpleName}")

      }

    }
  }
}
