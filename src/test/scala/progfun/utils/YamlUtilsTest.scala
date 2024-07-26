package progfun.utils

import progfun.models._
import progfun.models.outputs._

class YamlUtilsTest extends munit.FunSuite {

  test("toYaml - should generate YAML string from LawnOutput") {
    val lawnOutput = LawnOutput(
      limite = Point(5, 5),
      tondeuses = List(
        MowerOutput(
          debut = PositionOutput(Point(1, 2), 'N'),
          instructions = List('G', 'A', 'G', 'A', 'G', 'A', 'G', 'A', 'A'),
          fin = PositionOutput(Point(1, 3), 'N')
        ),
        MowerOutput(
          debut = PositionOutput(Point(3, 3), 'E'),
          instructions = List('A', 'A', 'D', 'A', 'A', 'D', 'A', 'D', 'D', 'A'),
          fin = PositionOutput(Point(5, 1), 'E')
        )
      )
    )

    val expectedYaml =
      """limite:
        |  x: 5
        |  y: 5
        |tondeuses:
        |  - debut:
        |      point:
        |        x: 1
        |        y: 2
        |      direction: N
        |    instructions:
        |      - G
        |      - A
        |      - G
        |      - A
        |      - G
        |      - A
        |      - G
        |      - A
        |      - A
        |    fin:
        |      point:
        |        x: 1
        |        y: 3
        |      direction: N
        |  - debut:
        |      point:
        |        x: 3
        |        y: 3
        |      direction: E
        |    instructions:
        |      - A
        |      - A
        |      - D
        |      - A
        |      - A
        |      - D
        |      - A
        |      - D
        |      - D
        |      - A
        |    fin:
        |      point:
        |        x: 5
        |        y: 1
        |      direction: E""".stripMargin

    val actualYaml = YamlUtils.toYaml(lawnOutput)

    assertEquals(actualYaml, expectedYaml)
  }

}
