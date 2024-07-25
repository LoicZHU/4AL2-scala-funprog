package fr.esgi.al.funprog

import java.util.Locale

import progfun.config._
import progfun.controllers._
import progfun.models._

@main
def Main(): Unit = {
  println("🚀 Program is running!")

  val config = AppConfig.load()
  run(config)
}

def run(config: AppConfig): Unit = {
  config.mode.trim().toUpperCase(Locale.getDefault()) match {
    case Mode.FULL => {
      MowerFullController.handleFullMode(config)
    }
    case Mode.STREAMING => {
      MowerStreamingController.runStreamingMode(config)
    }
    case _ => {
      println("💩 Mode invalide...")
    }
  }
}
