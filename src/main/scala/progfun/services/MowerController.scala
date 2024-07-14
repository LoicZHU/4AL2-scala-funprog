package progfun.services

import scala.annotation.tailrec
import scala.util._

import progfun.config._
import progfun.models._
import progfun.utils._
import upickle.default._

object MowerController {

  def executeMowers(config: AppConfig, lines: List[String]): Unit = {
    val absoluteProjectPath = System.getProperty("user.dir")

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
              instructions = initialMower.instructions.map(Instruction.toChar),
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

  def executeMowersStreaming(config: AppConfig): Unit = {
    val absoluteProjectPath = System.getProperty("user.dir")
    val bufferedSource = scala.io.Source.stdin.getLines()

    println("Please enter the lawn dimensions (e.g. '5 5'):")
    val lawnLine = bufferedSource.next()
    val lawn = parseLawn(lawnLine)
    val mowers = readMowers(bufferedSource, List.empty)

    this.generateOutputs(config, absoluteProjectPath, lawn, mowers.reverse)
  }

  @tailrec
  private def readMowers(
      bufferedSource: Iterator[String],
      acc: List[Mower]): List[Mower] = {
    println("Please enter the mower initial position (e.g., '1 2 N'):")
    if (!bufferedSource.hasNext) acc
    else {
      val positionLine = bufferedSource.next()
      if (positionLine.isEmpty) acc
      else {
        println("Please enter the instructions (e.g., 'GAGAGAGAA'):")
        if (!bufferedSource.hasNext) acc
        else {
          val instructionLine = bufferedSource.next()
          if (instructionLine.isEmpty) acc
          else {
            val mowerOption = for {
              x               <- positionLine.split(" ").headOption.map(_.toInt)
              y               <- positionLine.split(" ").lift(1).map(_.toInt)
              orientationChar <- positionLine.split(" ").lift(2).map(_.head)
              orientation <- Direction.fromChar(orientationChar) match {
                case Some(orientation) => Some(orientation)
                case None              => Some(South)
              }
            } yield Mower(
              Position(Point(x, y), orientation),
              instructionLine.flatMap(Instruction.fromChar).toList
            )

            val newAcc = mowerOption match {
              case Some(mower) => mower :: acc
              case None        => acc
            }
            readMowers(bufferedSource, newAcc)
          }
        }
      }
    }
  }

//  private def readMowers(bufferedSource: Iterator[String]): List[Mower] = {
//    val mowersBuffer = ListBuffer.empty[Mower]
//
//    while (bufferedSource.hasNext) {
//      println("Please enter the mower initial position (e.g., '1 2 N'):")
//      val positionLine = bufferedSource.next()
//      if (positionLine.isEmpty) return mowersBuffer.toList
//
//      println("Please enter the instructions (e.g., 'GAGAGAGAA'):")
//      val instructionLine = bufferedSource.next()
//      if (instructionLine.isEmpty) return mowersBuffer.toList
//
//      val mowerOption = for {
//        x               <- positionLine.split(" ").headOption.map(_.toInt)
//        y               <- positionLine.split(" ").lift(1).map(_.toInt)
//        orientationChar <- positionLine.split(" ").lift(2).map(_.head)
//        orientation <- Direction.fromChar(orientationChar) match {
//          case Some(orientation) => Some(orientation)
//          case None              => Some(South)
//        }
//      } yield Mower(
//        Position(Point(x, y), orientation),
//        instructionLine.flatMap(Instruction.fromChar).toList
//      )
//
//      mowerOption.foreach(mowersBuffer += _)
//    }
//
//    mowersBuffer.toList
//  }

  private def generateOutputs(
      config: AppConfig,
      absoluteProjectPath: String,
      lawn: Lawn,
      mowers: List[Mower]
  ): Unit = {
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
          instructions = initialMower.instructions.map(Instruction.toChar),
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
