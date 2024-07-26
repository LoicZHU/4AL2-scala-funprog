package progfun.services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util._

import progfun.config.AppConfig
import progfun.models.outputs._
import progfun.utils._

object LogService {
  def logFinalPositionOfMowers(
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
}
