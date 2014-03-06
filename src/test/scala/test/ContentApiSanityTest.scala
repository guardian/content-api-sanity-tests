package test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.typesafe.config.ConfigFactory


class ContentApiSanityTest extends FlatSpec with ShouldMatchers with Http {

  val CacheControl = """public, max-age=(\d+)""".r

  "Content api" should "serve gzipped" in {
    val conf = ConfigFactory.load()


    val connection = GET(
      conf.getString("content-api-sanity-tests.host")+"/search?format=json&api-key="+conf.getString("content-api-sanity-tests.api-key"),
      compress = true
    )

    connection.bodyFromGzip should include("results")
  }
}


