import com.gu.contentapi.sanity._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json

class MostViewedContainsItemsTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "Most Viewed" should "contain more than 10 items" in {

    handleException {
      val httpRequest = requestHost("/uk?show-most-viewed=true").get
      whenReady(httpRequest) { result =>
        assume(result.status == 200, "Service is down")
        val json = Json.parse(result.body)
        val mostViewedJson = (json \ "response" \ "mostViewed")
        val mostViewedList = mostViewedJson.as[List[Map[String, String]]]
        mostViewedList.length should be > 10
      }
    }(fail,testNames.head, tags)
  }

}

