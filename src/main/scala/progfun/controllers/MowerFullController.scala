package progfun.controllers

import scala.util._

import progfun.config._
import progfun.services.FileService
import progfun.services.MowerService
import progfun.utils.FunctionUtils

object MowerFullController {

  def handleFullMode(config: AppConfig): Unit = {
    println("🖲  Full mode selected")
    val fileLines =
      FileService.readFile(
        s"${FunctionUtils.getAbsoluteProjectPath}${config.inputPath}"
      )

    fileLines match {
      case Failure(ex) => {
        println(s"💩 An error occurred: ${ex.getMessage}")
      }
      case Success(lines) => {
        this.runFullMode(config, lines)
      }
    }
  }

  private def runFullMode(config: AppConfig, lines: List[String]): Unit = {
    lines.headOption match {
      case None => {
        println("💩 The input file is empty or incorrect.")
      }
      case Some(lawnLine) => {
        val lawn = MowerService.parseLawn(lawnLine)
        val mowers = FileService.parseMowers(lines.drop(1))
        val lawnOutput = MowerService.getLawnOutput(lawn, mowers)

        MowerService.generateOutputFiles(config, lawnOutput)
      }
    }
  }

}
