package progfun.models

import upickle.default._

final case class LawnOutput(topRight: Coordinate, mowers: List[MowerOutput])

object LawnOutput {
  implicit val reader: Reader[LawnOutput] = macroR
  implicit val writer: Writer[LawnOutput] = macroW
}
