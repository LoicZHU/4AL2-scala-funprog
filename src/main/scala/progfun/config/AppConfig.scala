package progfun.config

import scala.util.Failure
import scala.util.Success

import progfun.utils.ConfigUtils

final case class AppConfig(
    name: String,
    inputPath: String,
    jsonPath: String,
    csvPath: String,
    yamlPath: String
)

object AppConfig {

  def load(): AppConfig = {
    ConfigUtils.loadConfig("src/main/resources/config.json") match {
      case Failure(ex) =>
        println(s"💩 Failed to load config: ${ex.getMessage}")
        sys.exit(1)
      case Success(config) =>
        AppConfig(
          config.getOrElse("name", "funprog"),
          config.getOrElse("inputPath", "/tmp/input.txt"),
          config.getOrElse("jsonPath", "/tmp/output.json"),
          config.getOrElse("csvPath", "/tmp/output.csv"),
          config.getOrElse("yamlPath", "/tmp/output.yaml")
        )
    }
  }

}
