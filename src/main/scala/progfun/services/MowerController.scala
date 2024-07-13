package progfun.services

import progfun.config.AppConfig
import progfun.models._
import scala.util._

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
