package progfun.services

import scala.annotation.tailrec
import scala.util._

import progfun.config.AppConfig
import progfun.models._
import progfun.models.outputs._
import progfun.utils._
import upickle.default._

object MowerService {

  def parseLawn(line: String): Lawn = {
    val Array(x, y) = line.split(" ").map(_.toInt)

    if (this.areLawnDimensionsInvalid(x, y)) {
      println("💩 Lawn dimensions must be positive.")
      sys.exit(1)
    } else {
      Lawn(Point(x, y))
    }
  }

  def areLawnDimensionsInvalid(x: Int, y: Int): Boolean = {
    x < 0 || y < 0
  }

  def areLinesInvalid(lines: List[String]): Boolean = {
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

  def checkLawnLine(lawnLine: String): Unit = {
    if (lawnLine.isEmpty) {
      println("💩 Lawn dimensions cannot be empty.")
      sys.exit(1)
    } else {
      val parts = lawnLine.split(" ")
      if (parts.length != 2) {
        println("💩 Lawn dimensions must be in the format 'X Y'.")
        sys.exit(1)
      } else {
        val x = parts(0)
        val y = parts(1)

        (Try(x.toInt), Try(y.toInt)) match {
          case (Failure(_), _) | (_, Failure(_)) =>
            println("💩 Lawn dimensions must be integers.")
            sys.exit(1)
          case _ => ()
        }
      }
    }
  }

  def generateOutputFiles(config: AppConfig, lawnOutput: LawnOutput): Unit = {
    this.generateJsonFile(config, lawnOutput)
    this.generateCsvFile(config, lawnOutput)
    this.generateYamlFile(config, lawnOutput)
  }

  private def generateJsonFile(
      config: AppConfig,
      lawnOutput: LawnOutput): Unit = {
    val content = write(lawnOutput, indent = 2)
    val absoluteDestinationPath =
      s"${FunctionUtils.getAbsoluteProjectPath}${config.jsonPath}"

    FileService.writeFile(absoluteDestinationPath, content) match {
      case Failure(ex) => {
        println(s"💩 Failed to write JSON output: ${ex.getMessage}")
      }
      case Success(_) => {
        println("📦 JSON output done.")
      }
    }
  }

  private def generateYamlFile(
      config: AppConfig,
      lawnOutput: LawnOutput): Unit = {
    val content = YamlUtils.toYaml(lawnOutput)
    val absoluteDestinationPath =
      s"${FunctionUtils.getAbsoluteProjectPath}${config.yamlPath}"

    FileService.writeFile(absoluteDestinationPath, content) match {
      case Failure(ex) => {
        println(s"💩 Failed to write YAML output: ${ex.getMessage}")
      }
      case Success(_) => {
        println("📚 YAML output done.")
      }
    }
  }

  private def generateCsvFile(
      config: AppConfig,
      lawnOutput: LawnOutput): Unit = {
    val content = CsvUtils.toCsv(lawnOutput)
    val absoluteDestinationPath =
      s"${FunctionUtils.getAbsoluteProjectPath}${config.csvPath}"

    FileService.writeFile(absoluteDestinationPath, content) match {
      case Failure(ex) => {
        println(s"💩 Failed to write CSV output: ${ex.getMessage}")
      }
      case Success(_) => {
        println("📄 CSV output done.")
      }
    }
  }

  def getLawnOutput(lawn: Lawn, mowers: List[Mower]): LawnOutput = {
    val finalMowers = mowers.map { mower =>
      InstructionService.move(mower, lawn)
    }

    val mowerOutputs = this.getMowerOutputs(mowers, finalMowers)

    LawnOutput(
      Point(lawn.topRightBound.x, lawn.topRightBound.y),
      mowerOutputs
    )
  }

  def getMowerOutputs(
      mowers: List[Mower],
      finalMowers: List[Mower]): List[MowerOutput] = {
    val mowerOutputs = mowers
      .zip(finalMowers)
      .map { case (initialMower, finalMower) =>
        val initialPosition = PositionOutput(
          Point(
            initialMower.position.point.x,
            initialMower.position.point.y
          ),
          Direction.toChar(initialMower.position.direction)
        )
        val finalPosition = outputs.PositionOutput(
          Point(
            finalMower.position.point.x,
            finalMower.position.point.y
          ),
          Direction.toChar(finalMower.position.direction)
        )

        MowerOutput(
          debut = initialPosition,
          instructions = initialMower.instructions.map(Instruction.toChar),
          fin = finalPosition
        )
      }

    mowerOutputs
  }

  @tailrec
  def readUserInputMowers(
      bufferedSource: Iterator[String],
      userMowers: List[Mower]): List[Mower] = {
    println("📍 Enter the mower initial position (e.g. '1 2 N'):")

    if (!bufferedSource.hasNext) {
      userMowers
    } else {
      val positionLine = bufferedSource.next().trim()
      if (positionLine.isEmpty) {
        userMowers
      } else {
        println("🧭 Enter the instructions (e.g. 'GAGAGAGAA'):")

        if (!bufferedSource.hasNext) {
          userMowers
        } else {
          val instructionLine = bufferedSource.next().trim()
          if (instructionLine.isEmpty) {
            userMowers
          } else {
            val mowerOption = for {
              x               <- positionLine.split(" ").headOption.map(_.toInt)
              y               <- positionLine.split(" ").lift(1).map(_.toInt)
              orientationChar <- positionLine.split(" ").lift(2).map(_.head)
              orientation <- Direction.fromChar(orientationChar) match {
                case None              => Some(South)
                case Some(orientation) => Some(orientation)
              }
            } yield Mower(
              Position(Point(x, y), orientation),
              instructionLine.flatMap(Instruction.fromChar).toList
            )

            val newAcc = mowerOption match {
              case None        => userMowers
              case Some(mower) => mower :: userMowers
            }

            this.readUserInputMowers(bufferedSource, newAcc)
          }
        }
      }
    }
  }

}
