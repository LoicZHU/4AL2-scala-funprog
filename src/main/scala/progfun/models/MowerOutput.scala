package progfun.models

import upickle.default._

final case class MowerOutput(
    start: PositionOutput,
    instructions: List[Char],
    end: PositionOutput)

object MowerOutput {
  implicit val reader: Reader[MowerOutput] = macroR
  implicit val writer: Writer[MowerOutput] = macroW
}
