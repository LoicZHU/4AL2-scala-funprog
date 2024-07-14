package fr.esgi.al.funprog

import scala.util._

import progfun.config._
import progfun.services._

@main
def Main(): Unit = {
  println("🚀 Program is running!")
  val config = AppConfig.load()

  println("Select mode (1 = streaming, 2 = full): ")
  val mode = scala.io.StdIn.readInt()
  mode match {
    case 1 => {
      println("📡 Streaming mode selected")
      MowerController.executeMowersStreaming(config)
    }
    case 2 => {
      val fileContent = FileService.readFile(
        s"${System.getProperty("user.dir")}${config.inputPath}"
      )
      fileContent match {
        case Failure(ex) => {
          println(s"💩 An error occurred: ${ex.getMessage}")
        }
        case Success(lines) => {
          MowerController.executeMowers(config, lines)
        }
      }
    }
    case _ => {
      println("💩 Invalid mode selected")
    }
  }
}
