package progfun.models

sealed trait Direction {
  def shortName: Char
}

case object North extends Direction {
  val shortName: Char = 'N'
}
case object East extends Direction {
  val shortName: Char = 'E'
}
case object South extends Direction {
  val shortName: Char = 'S'
}
case object West extends Direction {
  val shortName: Char = 'W'
}

object Direction {
  def fromChar(character: Char): Option[Direction] = character match {
    case 'N' => Some(North)
    case 'E' => Some(East)
    case 'S' => Some(South)
    case 'W' => Some(West)
    case _   => None
  }

  def toChar(direction: Direction): Char = {
    direction.shortName
  }
}
