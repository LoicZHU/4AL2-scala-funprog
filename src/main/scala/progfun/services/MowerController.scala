package progfun.services

import scala.util._

import progfun.config.AppConfig
import progfun.models._
import upickle.default._

object MowerController {

  def executeMowers(): Unit = {
    val config = AppConfig.load()
    val fileContent = FileService.readFile(
      s"${System.getProperty("user.dir")}${config.inputPath}"
    )

    fileContent match {
      case Failure(ex) => {
        println(s"💩 An error occurred: ${ex.getMessage}")
      }
      case Success(lines) => {
        println("🚀 Starting the mowers...")

        lines.headOption match {
          case None => println("💩 The input file is empty or incorrect.")
          case Some(lawnLine) =>
            val lawn = parseLawn(lawnLine)
            val mowers = FileService.parseMowers(lines.drop(1))

            val finalMowers = mowers.map { mower =>
              InstructionService.move(mower, lawn)
            }

            val mowerOutputs =
              mowers.zip(finalMowers).map { case (initial, finalMower) =>
                MowerOutput(
                  start = PositionOutput(
                    Coordinate(
                      initial.position.coordinate.x,
                      initial.position.coordinate.y
                    ),
                    Orientation
                      .toChar(initial.position.orientation)
                      .toString
                  ),
                  instructions = initial.instructions.map(Instruction.toChar),
                  end = PositionOutput(
                    Coordinate(
                      finalMower.position.coordinate.x,
                      finalMower.position.coordinate.y
                    ),
                    Orientation
                      .toChar(finalMower.position.orientation)
                      .toString
                  )
                )
              }

            val lawnOutput = LawnOutput(
              Coordinate(lawn.topRight.x, lawn.topRight.y),
              mowerOutputs
            )

            val jsonString = write(lawnOutput)
            println(s"JSON output: ${jsonString}")
            FileService.writeFile(
              s"${System.getProperty("user.dir")}${config.jsonPath}",
              jsonString
            ) match {
              case Failure(ex) => {
                println(s"Failed to write JSON output: ${ex.getMessage}")
              }
              case Success(_) => {
                println(s"JSON output written to ${config.jsonPath}")
              }
            }

            finalMowers.foreach { mower =>
              val orientation = mower.position.orientation
              val (x, y) =
                (mower.position.coordinate.x, mower.position.coordinate.y)

              println(s"Final Position: ${x} ${y} ${orientation}")
            }
        }
      }

    }
  }

  private def parseLawn(line: String): Lawn = {
    val Array(x, y) = line.split(" ").map(_.toInt)
    Lawn(Coordinate(x, y))
  }
}
