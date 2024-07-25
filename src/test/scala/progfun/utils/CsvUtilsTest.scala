package progfun.utils

import progfun.models._
import progfun.models.outputs._

class CsvUtilsTest extends munit.FunSuite {

  test("toCsv - should generate CSV string from LawnOutput") {
    val lawnOutput = LawnOutput(
      limite = Point(5, 5),
      tondeuses = List(
        MowerOutput(
          debut = PositionOutput(Point(1, 2), Direction.toChar(North)),
          instructions = List(
            Instruction.toChar(Gauche),
            Instruction.toChar(Avancer),
            Instruction.toChar(Gauche),
            Instruction.toChar(Avancer),
            Instruction.toChar(Gauche),
            Instruction.toChar(Avancer),
            Instruction.toChar(Gauche),
            Instruction.toChar(Avancer),
            Instruction.toChar(Avancer)
          ),
          fin = PositionOutput(Point(1, 3), Direction.toChar(North))
        ),
        MowerOutput(
          debut = PositionOutput(Point(3, 3), Direction.toChar(East)),
          instructions = List(
            Instruction.toChar(Avancer),
            Instruction.toChar(Avancer),
            Instruction.toChar(Droite),
            Instruction.toChar(Avancer),
            Instruction.toChar(Avancer),
            Instruction.toChar(Droite),
            Instruction.toChar(Avancer),
            Instruction.toChar(Droite),
            Instruction.toChar(Droite),
            Instruction.toChar(Avancer)
          ),
          fin = PositionOutput(Point(5, 1), Direction.toChar(East))
        )
      )
    )

    val expectedCsv =
      """numéro,début_x,début_y,début_direction,fin_x,fin_y,fin_direction,instructions
        |1,1,2,N,1,3,N,GAGAGAGAA
        |2,3,3,E,5,1,E,AADAADADDA""".stripMargin

    val actualCsv = CsvUtils.toCsv(lawnOutput)

    assertEquals(actualCsv, expectedCsv)
  }

}
