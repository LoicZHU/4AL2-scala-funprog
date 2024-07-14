package progfun.models

import upickle.default._

final case class PositionOutput(point: Point, direction: Char)

object PositionOutput {
  implicit val reader: Reader[PositionOutput] = macroR
  implicit val writer: Writer[PositionOutput] = macroW
}
