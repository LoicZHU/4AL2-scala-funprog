package progfun.services

import scala.util._

import progfun.config.AppConfig
import progfun.models._
import progfun.utils._
import upickle.default._

object MowerController {

  def executeMowers(): Unit = {
    val config = AppConfig.load()
    val absoluteProjectPath = System.getProperty("user.dir")
    val fileContent =
      FileService.readFile(s"${absoluteProjectPath}${config.inputPath}")

    fileContent match {
      case Failure(ex) => {
        println(s"💩 An error occurred: ${ex.getMessage}")
      }
      case Success(lines) => {
        println("🚜 Starting the mowers...")

        lines.headOption match {
          case None => println("💩 The input file is empty or incorrect.")
          case Some(lawnLine) =>
            val lawn = parseLawn(lawnLine)
            val mowers = FileService.parseMowers(lines.drop(1))

            val finalMowers = mowers.map { mower =>
              InstructionService.move(mower, lawn)
            }

            val mowerOutputs =
              mowers.zip(finalMowers).map { case (initialMower, finalMower) =>
                val initialPosition = PositionOutput(
                  Point(
                    initialMower.position.point.x,
                    initialMower.position.point.y
                  ),
                  Direction.toChar(initialMower.position.direction)
                )
                val finalPosition = PositionOutput(
                  Point(
                    finalMower.position.point.x,
                    finalMower.position.point.y
                  ),
                  Direction.toChar(finalMower.position.direction)
                )

                MowerOutput(
                  debut = initialPosition,
                  instructions =
                    initialMower.instructions.map(Instruction.toChar),
                  fin = finalPosition
                )
              }

            val lawnOutput = LawnOutput(
              Point(lawn.topRightBound.x, lawn.topRightBound.y),
              mowerOutputs
            )

            val jsonString = write(lawnOutput, indent = 2)
            val jsonPath = s"${absoluteProjectPath}${config.jsonPath}"
            this.generateJsonFile(jsonString, jsonPath)

            val csvString = CsvUtils.toCsv(lawnOutput)
            val csvPath = s"${absoluteProjectPath}${config.csvPath}"
            this.generateCsvFile(csvString, csvPath)

            val yamlString = YamlUtils.toYaml(lawnOutput)
            val yamlPath = s"${absoluteProjectPath}${config.yamlPath}"
            this.generateYamlFile(yamlString, yamlPath)

            this.printFinalPosition(finalMowers)
        }
      }

    }
  }

  private def parseLawn(line: String): Lawn = {
    val Array(x, y) = line.split(" ").map(_.toInt)
    Lawn(Point(x, y))
  }

  private def generateJsonFile(jsonString: String, jsonPath: String): Unit = {
    FileService.writeFile(jsonPath, jsonString) match {
      case Failure(ex) => {
        println(s"💩 Failed to write JSON output: ${ex.getMessage}")
      }
      case Success(_) => {
        println("📦 JSON output done.")
      }
    }
  }

  private def generateYamlFile(yamlString: String, yamlPath: String): Unit = {
    FileService.writeFile(yamlPath, yamlString) match {
      case Failure(ex) => {
        println(s"💩 Failed to write YAML output: ${ex.getMessage}")
      }
      case Success(_) => {
        println("📚 YAML output done.")
      }
    }
  }

  private def generateCsvFile(csvString: String, csvPath: String): Unit = {
    FileService.writeFile(csvPath, csvString) match {
      case Failure(ex) => {
        println(s"💩 Failed to write CSV output: ${ex.getMessage}")
      }
      case Success(_) => {
        println("📄 CSV output done.")
      }
    }
  }

  private def printFinalPosition(finalMowers: List[Mower]): Unit = {
    finalMowers.foreach { mower =>
      val orientation = mower.position.direction
      val (x, y) = (mower.position.point.x, mower.position.point.y)

      println(s"📍 Final position: ${x} ${y} ${orientation}")
    }
  }

}
