package fr.esgi.al.funprog

import scala.io.StdIn._

import progfun.config._
import progfun.services._

@main
def Main(): Unit = {
  println("🚀 Program is running!")

  val config = AppConfig.load()
  inviteUserToModeSelection(config)
}

def inviteUserToModeSelection(config: AppConfig): Unit = {
  println("🕹  Select mode (1 = streaming, 2 = full): ")

  val mode = readInt()
  mode match {
    case 1 => MowerController.runStreamingMode(config)
    case 2 => MowerController.handleFullMode(config)
    case _ => println("💩 Invalid mode selected")
  }
}
