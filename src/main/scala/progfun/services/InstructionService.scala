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
    val newCoordinate = position.direction match {
      case North => position.point.copy(y = position.point.y + 1)
      case East  => position.point.copy(x = position.point.x + 1)
      case South => position.point.copy(y = position.point.y - 1)
      case West  => position.point.copy(x = position.point.x - 1)
    }

    if (!this.isWithinBounds(newCoordinate, lawn)) {
      position
    } else {
      position.copy(point = newCoordinate)
    }
  }

  private def isWithinBounds(coordinate: Point, lawn: Lawn): Boolean = {
    coordinate.x >= 0 && coordinate.x <= lawn.topRightBound.x &&
    coordinate.y >= 0 && coordinate.y <= lawn.topRightBound.y
  }

  private def turnLeft(position: Position): Position = {
    val newOrientation = position.direction match {
      case North => West
      case East  => North
      case South => East
      case West  => South
    }

    position.copy(direction = newOrientation)
  }

  private def turnRight(position: Position): Position = {
    val newOrientation = position.direction match {
      case North => East
      case East  => South
      case South => West
      case West  => North
    }

    position.copy(direction = newOrientation)
  }

}
