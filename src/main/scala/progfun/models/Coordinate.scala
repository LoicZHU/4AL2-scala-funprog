package progfun.models

import upickle.default._

final case class Coordinate(x: Int, y: Int)

object Coordinate {
//  def apply(x: Int, y: Int): Coordinate = new Coordinate(x, y)
  implicit val reader: Reader[Coordinate] = macroR
  implicit val writer: Writer[Coordinate] = macroW
}
