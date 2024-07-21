package progfun.utils

import scala.io.Source
import scala.util.Try

object ConfigUtils {

  def loadConfig(filePath: String): Try[Map[String, String]] = Try {
    val source = Source.fromFile(filePath, "UTF-8")

    try {
      parseJson(source.mkString)
    } finally {
      source.close()
    }
  }

  private def parseJson(jsonString: String): Map[String, String] = {
    val nonNullJsonString = Option(jsonString).getOrElse("")

    nonNullJsonString
      .trim()
      .stripPrefix("{")
      .stripSuffix("}")
      .split(",")
      .map(_.split(":").map(_.trim.stripPrefix("\"").stripSuffix("\"")))
      .map { case Array(k, v) => (k, v) }
      .toMap
  }
}
