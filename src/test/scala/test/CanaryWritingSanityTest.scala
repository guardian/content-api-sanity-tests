package test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import scalaj.http.Http

class CanaryWritingSanityTest extends FlatSpec with ShouldMatchers {
  "Writing a collection" should "return a 202 response code" in {
    println(Http.get("http://www.google.com/").responseCode)

  }

}
