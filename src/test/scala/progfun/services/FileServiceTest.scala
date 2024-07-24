package progfun.services

import java.io._

import scala.io.Source
import scala.util._

import progfun.models._

class FileServiceTest extends munit.FunSuite {

  private val testDirectory = new File("test-files")

  override def beforeAll(): Unit = {
    if (testDirectory.exists()) {
      ()
    } else {
      val created = testDirectory.mkdir()
      if (!created) {
        println("💩 Failed to create test directory")
        sys.exit(1)
      }
    }
  }

  override def afterAll(): Unit = {
    val filesDeleted =
      testDirectory.listFiles().map(_.delete()).forall(_ == true)

    if (!filesDeleted) {
      println("💩 Failed to delete one or more test files")
      sys.exit(1)
    } else {
      val directoryDeleted = testDirectory.delete()

      if (directoryDeleted) {
        ()
      } else {
        println("💩 Failed to delete test directory")
        sys.exit(1)
      }
    }
  }

  test("readFile - should read the content of a file successfully") {
    val testFile = new File(testDirectory, "readFileTest.txt")
    writeToFile(testFile, "line1\nline2\nline3")

    val result = FileService.readFile(testFile.getPath)

    result match {
      case Failure(ex) => {
        fail(s"Unexpected failure: ${ex.getMessage}")
      }
      case Success(lines) => {
        assertEquals(lines, List("line1", "line2", "line3"))
      }
    }
  }

  test("readFile - should handle file reading errors") {
    val result = FileService.readFile("nonExistentFile.txt")

    assert(result.isFailure)
  }

  test("writeFile - should write content to a file successfully") {
    val testFile = new File(testDirectory, "writeFileTest.txt")
    val content = "Hello, world!"

    val result = FileService.writeFile(testFile.getPath, content)

    result match {
      case Failure(ex) => {
        fail(s"Unexpected failure: ${ex.getMessage}")
      }
      case Success(_) => {
        val lines =
          Source.fromFile(testFile, "UTF-8").getLines().toList
        assertEquals(lines, List(content))
      }
    }
  }

  test("writeFile - should handle file writing errors") {
    val result = FileService.writeFile(
      "/nonExistentDirectory/writeFileTest.txt",
      "content"
    )

    assert(result.isFailure)
  }

  test("upsertLogFile - should append content to a log file successfully") {
    val testFile = new File(testDirectory, "upsertLogFileTest.txt")
    writeToFile(testFile, "initial content\n")
    val contentToAppend = "appended content\n"

    val result = FileService.upsertLogFile(testFile.getPath, contentToAppend)

    result match {
      case Failure(ex) => {
        fail(s"Unexpected failure: ${ex.getMessage}")
      }
      case Success(_) => {
        val lines =
          Source.fromFile(testFile, "UTF-8").getLines().toList
        assertEquals(lines, List("initial content", "appended content"))
      }
    }
  }

  test("upsertLogFile - should handle errors when appending to a log file") {
    val result = FileService.upsertLogFile(
      "/nonExistentDirectory/upsertLogFileTest.txt",
      "content"
    )

    assert(result.isFailure)
  }

  test("parseMowers - should parse mowers correctly from lines") {
    val lines = List("1 2 N", "GAGAGAGAA", "3 3 E", "AADAADADDA")
    val expectedMowers = List(
      Mower(
        Position(Point(1, 2), North),
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
      ),
      Mower(
        Position(Point(3, 3), East),
        List(
          Avancer,
          Avancer,
          Droite,
          Avancer,
          Avancer,
          Droite,
          Avancer,
          Droite,
          Droite,
          Avancer
        )
      )
    )

    val mowers = FileService.parseMowers(lines)

    assertEquals(mowers, expectedMowers)
  }

  private def writeToFile(file: File, content: String): Unit = {
    val writer = PrintWriter(file)
    try {
      writer.write(content)
    } finally {
      writer.close()
    }
  }

}
