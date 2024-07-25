package progfun.services

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

}
