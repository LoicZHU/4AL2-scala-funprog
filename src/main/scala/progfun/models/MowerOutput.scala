package progfun.models

import upickle.default._

final case class MowerOutput(
    debut: PositionOutput,
    instructions: List[Char],
    fin: PositionOutput)

object MowerOutput {
  implicit val reader: Reader[MowerOutput] = macroR
  implicit val writer: Writer[MowerOutput] = macroW
}
