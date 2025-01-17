package progfun.models

sealed trait Instruction {
  def shortName: Char
}

case object Avancer extends Instruction {
  val shortName: Char = 'A'
}
case object Gauche extends Instruction {
  val shortName: Char = 'G'
}
case object Droite extends Instruction {
  val shortName: Char = 'D'
}

object Instruction {
  def fromChar(character: Char): Option[Instruction] = character.toUpper match {
    case 'A' => Some(Avancer)
    case 'G' => Some(Gauche)
    case 'D' => Some(Droite)
    case _   => None
  }

  def toChar(instruction: Instruction): Char = {
    instruction.shortName
  }
}
