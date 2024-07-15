package progfun.models.outputs

import progfun.models._
import upickle.default._

final case class LawnOutput(limite: Point, tondeuses: List[MowerOutput])

object LawnOutput {
  implicit val reader: Reader[LawnOutput] = macroR
  implicit val writer: Writer[LawnOutput] = macroW
}
