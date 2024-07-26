package progfun.controllers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.annotation.tailrec
import scala.io.Source._
import scala.util._

import progfun.config._
import progfun.models._
import progfun.models.outputs._
import progfun.services.FileService
import progfun.services.MowerService
import progfun.utils.FunctionUtils

object MowerStreamingController {

  def runStreamingMode(config: AppConfig): Unit = {
    try {
      println("📡 Streaming mode selected")
      val bufferedSource = stdin.getLines()

      println("🗺  Enter the lawn dimensions (e.g. '5 5'):")
      val lawnLine = bufferedSource.next().trim()
      this.checkLawnLine(lawnLine)

      val lawn = MowerService.parseLawn(lawnLine)
      val reversedMowers =
        this.readUserInputMowers(bufferedSource, List.empty).reverse
      val lawnOutput = MowerService.getLawnOutput(lawn, reversedMowers)

      MowerService.generateOutputFiles(config, lawnOutput)
      this.logFinalPositionOfMowers(config, lawnOutput)
    } catch {
      case throwable: Throwable => {
        println(s"💩 An error occurred: ${throwable.getMessage}")
      }
    }
  }

  private def checkLawnLine(lawnLine: String): Unit = {
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

  private def logFinalPositionOfMowers(
      config: AppConfig,
      lawnOutput: LawnOutput): Unit = {
    val absoluteDestinationPath =
      s"${FunctionUtils.getAbsoluteProjectPath}${config.logPath}"
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

  @tailrec
  private def readUserInputMowers(
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
