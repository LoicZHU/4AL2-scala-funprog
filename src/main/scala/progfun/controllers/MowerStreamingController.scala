package progfun.controllers

import scala.io.Source._

import progfun.config._
import progfun.services._

object MowerStreamingController {

  def handle(config: AppConfig): Unit = {
    try {
      println("📡 Streaming mode selected")
      val bufferedSource = stdin.getLines()

      println("🗺  Enter the lawn dimensions (e.g. '5 5'):")
      val lawnLine = bufferedSource.next().trim()
      MowerService.checkLawnLine(lawnLine)

      val lawn = MowerService.parseLawn(lawnLine)
      val reversedMowers =
        MowerService.readUserInputMowers(bufferedSource, List.empty).reverse
      val lawnOutput = MowerService.getLawnOutput(lawn, reversedMowers)

      MowerService.generateOutputFiles(config, lawnOutput)
      LogService.logFinalPositionOfMowers(config, lawnOutput)
    } catch {
      case throwable: Throwable => {
        println(s"💩 An error occurred: ${throwable.getMessage}")
      }
    }
  }

}
