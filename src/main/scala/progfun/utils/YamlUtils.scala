package progfun.utils

import progfun.models._
import progfun.models.outputs._

object YamlUtils {
  def toYaml(lawnOutput: LawnOutput): String = {
    val header =
      s"limite:\n  x: ${lawnOutput.limite.x}\n  y: ${lawnOutput.limite.y}"

    val mowersYaml = lawnOutput.tondeuses
      .map { case (mower) =>
        val start = mower.debut
        val end = mower.fin
        val instructions =
          mower.instructions.map(ins => s"|      - $ins").mkString("\n")

        s"""  - debut:
          |      point:
          |        x: ${start.point.x}
          |        y: ${start.point.y}
          |      direction: ${start.direction}
          |    instructions:
          ${instructions}
          |    fin:
          |      point:
          |        x: ${end.point.x}
          |        y: ${end.point.y}
          |      direction: ${end.direction}""".stripMargin
      }
      .mkString("\n")

    s"${header}\ntondeuses:\n${mowersYaml}"
  }
}
