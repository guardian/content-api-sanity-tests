package test
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io.Source._

trait JsonFileLoader {
  def loadJson(source: String): JValue = parse(fromFile(source).mkString)
}
