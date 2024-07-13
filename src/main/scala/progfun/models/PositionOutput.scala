package progfun.models

import upickle.default._

final case class PositionOutput(coordinate: Coordinate, orientation: String)

object PositionOutput {
//  def apply(coordinate: Coordinate, orientation: Orientation): PositionOutput = {
//    PositionOutput(coordinate, orientation.toString)
//  }
  implicit val reader: Reader[PositionOutput] = macroR
  implicit val writer: Writer[PositionOutput] = macroW
}
