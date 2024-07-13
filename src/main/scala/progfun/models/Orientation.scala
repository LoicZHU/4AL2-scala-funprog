package progfun.models

sealed trait Orientation {
  def shortName: Char
}

case object North extends Orientation {
  val shortName: Char = 'N'
}
case object East extends Orientation {
  val shortName: Char = 'E'
}
case object South extends Orientation {
  val shortName: Char = 'S'
}
case object West extends Orientation {
  val shortName: Char = 'W'
}

object Orientation {
  def fromChar(character: Char): Option[Orientation] = character match {
    case 'N' => Some(North)
    case 'E' => Some(East)
    case 'S' => Some(South)
    case 'W' => Some(West)
    case _   => None
  }

  def toChar(orientation: Orientation): Char = {
    orientation.shortName
  }
}
