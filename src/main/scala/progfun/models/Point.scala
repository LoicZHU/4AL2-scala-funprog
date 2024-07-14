package progfun.models

import upickle.default._

final case class Point(x: Int, y: Int)

object Point {
  implicit val reader: Reader[Point] = macroR
  implicit val writer: Writer[Point] = macroW
}
