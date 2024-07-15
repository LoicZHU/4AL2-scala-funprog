package progfun.services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.annotation._
import scala.io.Source._
import scala.util._

import progfun.config._
import progfun.models._
import progfun.models.outputs._
import progfun.utils._
import upickle.default._

object MowerController {

  private val ABSOLUTE_PROJECT_PATH = System.getProperty("user.dir")

  def handleFullMode(config: AppConfig): Unit = {
    println("🖲  Full mode selected")
    val fileLines =
      FileService.readFile(s"${ABSOLUTE_PROJECT_PATH}${config.inputPath}")

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
        val lawn = parseLawn(lawnLine)
        val mowers = FileService.parseMowers(lines.drop(1))
        val lawnOutput = this.getLawnOutput(lawn, mowers)

        this.generateOutputFiles(config, lawnOutput)
      }
    }
  }

  private def parseLawn(line: String): Lawn = {
    val Array(x, y) = line.split(" ").map(_.toInt)

    Lawn(Point(x, y))
  }

  private def generateOutputFiles(
      config: AppConfig,
      lawnOutput: LawnOutput): Unit = {
    this.generateJsonFile(config, lawnOutput)
    this.generateCsvFile(config, lawnOutput)
    this.generateYamlFile(config, lawnOutput)
  }

  private def getLawnOutput(lawn: Lawn, mowers: List[Mower]) = {
    val finalMowers = mowers.map { mower =>
      InstructionService.move(mower, lawn)
    }

    val mowerOutputs = this.getMowerOutputs(mowers, finalMowers)

    LawnOutput(
      Point(lawn.topRightBound.x, lawn.topRightBound.y),
      mowerOutputs
    )
  }

  private def getMowerOutputs(
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

  private def generateJsonFile(
      config: AppConfig,
      lawnOutput: LawnOutput): Unit = {
    val content = write(lawnOutput, indent = 2)
    val absoluteDestinationPath = s"${ABSOLUTE_PROJECT_PATH}${config.jsonPath}"

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
    val absoluteDestinationPath = s"${ABSOLUTE_PROJECT_PATH}${config.yamlPath}"

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
    val absoluteDestinationPath = s"${ABSOLUTE_PROJECT_PATH}${config.csvPath}"

    FileService.writeFile(absoluteDestinationPath, content) match {
      case Failure(ex) => {
        println(s"💩 Failed to write CSV output: ${ex.getMessage}")
      }
      case Success(_) => {
        println("📄 CSV output done.")
      }
    }
  }

  private def logFinalPositionOfMowers(
      config: AppConfig,
      lawnOutput: LawnOutput): Unit = {
    val absoluteDestinationPath = s"${ABSOLUTE_PROJECT_PATH}${config.logPath}"
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val logContent = lawnOutput.tondeuses
      .map { case (mowerOutput) =>
        val start = mowerOutput.debut
        val end = mowerOutput.fin
        val instructions: String = mowerOutput.instructions.mkString("")

        val currentDateTime = LocalDateTime.now()
        val formattedDateTime = currentDateTime.format(dateTimeFormatter)

        s"[${formattedDateTime}] 🏁 ${start.point.x} ${start.point.y} ${start.direction} (${instructions}): ${end.point.x} ${end.point.y} ${end.direction}\n"
      }
      .mkString("")

    FileService.upsertLogFile(
      absoluteDestinationPath,
      logContent
    ) match {
      case Failure(ex) =>
        println(s"💩 Failed to log final position: ${ex.getMessage}")
      case Success(_) =>
        println("📝 Log done.")
    }
  }

  def runStreamingMode(config: AppConfig): Unit = {
    println("📡 Streaming mode selected")
    val bufferedSource = stdin.getLines()

    println("🗺  Enter the lawn dimensions (e.g. '5 5'):")
    val lawnLine = bufferedSource.next()
    val lawn = parseLawn(lawnLine)

    val reversedMowers =
      readUserInputMowers(bufferedSource, List.empty).reverse
    val lawnOutput = this.getLawnOutput(lawn, reversedMowers)

    this.generateOutputFiles(config, lawnOutput)
    this.logFinalPositionOfMowers(config, lawnOutput)
  }

  @tailrec
  private def readUserInputMowers(
      bufferedSource: Iterator[String],
      userMowers: List[Mower]): List[Mower] = {
    println("📍 Enter the mower initial position (e.g. '1 2 N'):")

    if (!bufferedSource.hasNext) {
      userMowers
    } else {
      val positionLine = bufferedSource.next()
      if (positionLine.isEmpty) {
        userMowers
      } else {
        println("🧭 Enter the instructions (e.g. 'GAGAGAGAA'):")

        if (!bufferedSource.hasNext) {
          userMowers
        } else {
          val instructionLine = bufferedSource.next()
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

            readUserInputMowers(bufferedSource, newAcc)
          }
        }
      }
    }
  }

}
