package progfun.services

import progfun.models._

class InstructionServiceTest extends munit.FunSuite {

  test("advance - should move the mower forward when within bounds") {
    val lawn = Lawn(Point(5, 5))
    val initialPosition = Position(Point(1, 2), North)
    val mower = Mower(initialPosition, List(Avancer))

    val movedMower = InstructionService.move(mower, lawn)
    assertEquals(movedMower.position.point, Point(1, 3))
  }

  test("advance - should not move the mower forward when out of bounds") {
    val lawn = Lawn(Point(5, 5))
    val initialPosition = Position(Point(1, 5), North)
    val mower = Mower(initialPosition, List(Avancer))

    val movedMower = InstructionService.move(mower, lawn)
    assertEquals(movedMower.position.point, Point(1, 5))
  }

  test("turnLeft - should turn the mower to the left") {
    val lawn = Lawn(Point(5, 5))
    val initialPosition = Position(Point(1, 2), North)
    val mower = Mower(initialPosition, List(Gauche))

    val movedMower = InstructionService.move(mower, lawn)
    assertEquals(movedMower.position.direction, West)
  }

  test("turnRight - should turn the mower to the right") {
    val lawn = Lawn(Point(5, 5))
    val initialPosition = Position(Point(1, 2), North)
    val mower = Mower(initialPosition, List(Droite))

    val movedMower = InstructionService.move(mower, lawn)
    assertEquals(movedMower.position.direction, East)
  }

  test(
    "complex instructions - should handle a sequence of movements and turns"
  ) {
    val lawn = Lawn(Point(5, 5))
    val initialPosition = Position(Point(1, 2), North)
    val mower = Mower(
      initialPosition,
      List(
        Gauche,
        Avancer,
        Gauche,
        Avancer,
        Gauche,
        Avancer,
        Gauche,
        Avancer,
        Avancer
      )
    )

    val movedMower = InstructionService.move(mower, lawn)
    assertEquals(movedMower.position.point, Point(1, 3))
    assertEquals(movedMower.position.direction, North)
  }

  test(
    "advance - should stay within bounds when attempting to move out of bounds (north)"
  ) {
    val lawn = Lawn(Point(5, 5))
    val initialPosition = Position(Point(2, 5), North)
    val mower = Mower(initialPosition, List(Avancer))

    val movedMower = InstructionService.move(mower, lawn)
    assertEquals(movedMower.position.point, Point(2, 5))
  }

  test(
    "advance - should stay within bounds when attempting to move out of bounds (east)"
  ) {
    val lawn = Lawn(Point(5, 5))
    val initialPosition = Position(Point(5, 2), East)
    val mower = Mower(initialPosition, List(Avancer))

    val movedMower = InstructionService.move(mower, lawn)
    assertEquals(movedMower.position.point, Point(5, 2))
  }

  test(
    "advance - should stay within bounds when attempting to move out of bounds (south)"
  ) {
    val lawn = Lawn(Point(5, 5))
    val initialPosition = Position(Point(2, 0), South)
    val mower = Mower(initialPosition, List(Avancer))

    val movedMower = InstructionService.move(mower, lawn)
    assertEquals(movedMower.position.point, Point(2, 0))
  }

  test(
    "advance - should stay within bounds when attempting to move out of bounds (west)"
  ) {
    val lawn = Lawn(Point(5, 5))
    val initialPosition = Position(Point(0, 2), West)
    val mower = Mower(initialPosition, List(Avancer))

    val movedMower = InstructionService.move(mower, lawn)
    assertEquals(movedMower.position.point, Point(0, 2))
  }

}
