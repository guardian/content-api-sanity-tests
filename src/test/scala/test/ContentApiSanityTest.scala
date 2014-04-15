package com.gu.contentapi.sanity

import org.scalatest.{Matchers, FlatSpec, matchers}

class ContentApiSanityTest extends FlatSpec with Matchers with HttpHandler {

  val CacheControl = """public, max-age=(\d+)""".r

  "Content api" should "serve gzipped" in {



    val connection = GET(
      Config.host+"search?format=json&api-key="+Config.apiKey,
      compress = true
    )

    connection.bodyFromGzip should include("results")
  }
}


