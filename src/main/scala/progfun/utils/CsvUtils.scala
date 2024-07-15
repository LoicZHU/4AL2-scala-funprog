package progfun.utils

import progfun.models._
import progfun.models.outputs._

object CsvUtils {
  def toCsv(lawnOutput: LawnOutput): String = {
    val header =
      "numéro,début_x,début_y,début_direction,fin_x,fin_y,fin_direction,instructions"

    val rows = lawnOutput.tondeuses.zipWithIndex.map { case (mowerOutput, i) =>
      val start = mowerOutput.debut
      val end = mowerOutput.fin
      val instructions: String = mowerOutput.instructions.mkString("")

      s"${i + 1},${start.point.x},${start.point.y},${start.direction},${end.point.x},${end.point.y},${end.direction},${instructions}"
    }

    (header :: rows).mkString("\n")
  }
}
