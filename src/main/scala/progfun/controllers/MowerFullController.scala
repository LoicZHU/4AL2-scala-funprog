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
        if (this.areLinesInvalid(lines)) {
          println("💩 The input file is incorrect.")
        } else {
          this.runFullMode(config, lines)
        }
      }
    }
  }

  private def areLinesInvalid(lines: List[String]) = {
    lines.isEmpty || !this.isValidLawnDimensions(lines.headOption.getOrElse(""))
  }

  private def isValidLawnDimensions(line: String): Boolean = {
    val parts = line.trim.split(" ")

    if (parts.length != 2) {
      false
    } else {
      try {
        val x: Int = parts(0).toInt
        val y: Int = parts(1).toInt

        x >= 0 && y >= 0
      } catch {
        case _: NumberFormatException => false
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
