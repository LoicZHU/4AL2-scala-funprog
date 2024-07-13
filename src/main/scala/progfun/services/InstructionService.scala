package progfun.services

import progfun.models._

object InstructionService {

  def move(mower: Mower, lawn: Lawn): Mower = {
    val finalPosition = mower.instructions.foldLeft(mower.position) {
      (currentPosition, instruction) =>
        executeInstruction(currentPosition, instruction, lawn)
    }

    mower.copy(
      position = finalPosition,
      instructions = mower.instructions
    )
  }

  private def executeInstruction(
      position: Position,
      instruction: Instruction,
      lawn: Lawn): Position = {
    instruction match {
      case Avancer => advance(position, lawn)
      case Gauche  => turnLeft(position)
      case Droite  => turnRight(position)
    }
  }

  private def advance(position: Position, lawn: Lawn): Position = {
    val newCoordinate = position.orientation match {
      case North => position.coordinate.copy(y = position.coordinate.y + 1)
      case East  => position.coordinate.copy(x = position.coordinate.x + 1)
      case South => position.coordinate.copy(y = position.coordinate.y - 1)
      case West  => position.coordinate.copy(x = position.coordinate.x - 1)
    }

    if (!this.isWithinBounds(newCoordinate, lawn)) {
      position
    } else {
      position.copy(coordinate = newCoordinate)
    }
  }

  private def isWithinBounds(coordinate: Coordinate, lawn: Lawn): Boolean = {
    coordinate.x >= 0 && coordinate.x <= lawn.topRight.x &&
    coordinate.y >= 0 && coordinate.y <= lawn.topRight.y
  }

  private def turnLeft(position: Position): Position = {
    val newOrientation = position.orientation match {
      case North => West
      case East  => North
      case South => East
      case West  => South
    }

    position.copy(orientation = newOrientation)
  }

  private def turnRight(position: Position): Position = {
    val newOrientation = position.orientation match {
      case North => East
      case East  => South
      case South => West
      case West  => North
    }

    position.copy(orientation = newOrientation)
  }

}
