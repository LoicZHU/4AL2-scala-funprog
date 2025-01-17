package progfun.services

import java.io._

import scala.io.Source
import scala.util.Try

import progfun.models._

object FileService {

  def readFile(filePath: String): Try[List[String]] = Try {
    val source = Source.fromFile(filePath, "UTF-8")

    try {
      source.getLines().toList
    } finally {
      source.close()
    }
  }

  def writeFile(filePath: String, content: String): Try[Unit] = Try {
    val writer = PrintWriter(filePath, "UTF-8")

    try {
      writer.write(content)
    } finally {
      writer.close()
    }
  }

  def upsertLogFile(
      destinationPath: String,
      content: String
  ): Try[Unit] = Try {
    val fileWriter = FileWriter(destinationPath, true)
    val printWriter = PrintWriter(fileWriter, true)

    try {
      printWriter.write(content)
    } finally {
      fileWriter.close()
      printWriter.close()
    }
  }

  def parseMowers(lines: List[String]): List[Mower] = {
    val mowers = lines.grouped(2).toList

    mowers.flatMap {
      case List(positionLine, instructionLine) => {
        if (this.IsMowerInvalid(positionLine, instructionLine)) {
          println(s"⚠️ Invalid mower: $positionLine, $instructionLine")
          None
        } else {
          val mowerPosition = positionLine.split(" ")
          val instructions =
            instructionLine.flatMap(Instruction.fromChar).toList

          for {
            x               <- mowerPosition.headOption.map(_.toInt)
            y               <- mowerPosition.lift(1).map(_.toInt)
            orientationChar <- mowerPosition.lift(2).map(_.head)
            orientation <- Direction.fromChar(orientationChar) match {
              case Some(orientation) => Some(orientation)
              case None              => Some(South)
            }
          } yield Mower(Position(Point(x, y), orientation), instructions)
        }
      }
      case _ => {
        None
      }
    }
  }

  private def IsMowerInvalid(positionLine: String, instructionLine: String) = {
    !this.isValidPositionLine(positionLine) || !this.isValidInstructionLine(
      instructionLine
    )
  }

  private def isValidPositionLine(line: String): Boolean = {
    val parts = line.split(" ")

    if (parts.length != 3) {
      false
    } else {
      try {
        val x = parts(0).toInt
        val y = parts(1).toInt
        val orientation =
          parts(2).headOption.flatMap(Direction.fromChar).isDefined

        x >= 0 && y >= 0 && orientation
      } catch {
        case _: NumberFormatException => false
      }
    }
  }

  private def isValidInstructionLine(line: String): Boolean = {
    line.forall(character => Instruction.fromChar(character).isDefined)
  }
}
