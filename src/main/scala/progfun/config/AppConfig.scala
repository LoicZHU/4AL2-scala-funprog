package progfun.config

import scala.util._

import progfun.models._
import progfun.utils.ConfigUtils

final case class AppConfig(
    name: String,
    mode: String,
    csvPath: String,
    inputPath: String,
    jsonPath: String,
    logPath: String,
    yamlPath: String
)

object AppConfig {

  def load(): AppConfig = {
    ConfigUtils.loadConfig("src/main/resources/config.json") match {
      case Failure(ex) => {
        println(s"💩 Failed to load config: ${ex.getMessage}")
        sys.exit(1)
      }
      case Success(config) => {
        AppConfig(
          config.getOrElse("name", "funprog"),
          config.getOrElse("mode", Mode.FULL),
          config.getOrElse("csvPath", "/tmp/output.csv"),
          config.getOrElse("inputPath", "/tmp/input.txt"),
          config.getOrElse("jsonPath", "/tmp/output.json"),
          config.getOrElse("logPath", "/tmp/output.log"),
          config.getOrElse("yamlPath", "/tmp/output.yaml")
        )
      }
    }
  }

}
