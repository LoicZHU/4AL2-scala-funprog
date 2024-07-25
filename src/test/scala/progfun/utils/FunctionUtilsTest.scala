package progfun.utils

class FunctionUtilsTest extends munit.FunSuite {

  test(
    "FunctionUtils.getAbsoluteProjectPath should return the correct project path"
  ) {
    val expectedPath = System.getProperty("user.dir")
    val actualPath = FunctionUtils.getAbsoluteProjectPath

    assertEquals(actualPath, expectedPath)
  }

  test("FunctionUtils.getAbsoluteProjectPath should not return null") {
    val pathOption = Option(FunctionUtils.getAbsoluteProjectPath)

    assert(pathOption.isDefined, "Path should not be null")
  }

  test(
    "FunctionUtils.getAbsoluteProjectPath should return a non-empty string"
  ) {
    val path = FunctionUtils.getAbsoluteProjectPath

    assert(path.nonEmpty, "Expected a non-empty string")
  }

  test(
    "FunctionUtils.getAbsoluteProjectPath should return a valid directory path"
  ) {
    val path = FunctionUtils.getAbsoluteProjectPath
    val dir = new java.io.File(path)

    assert(dir.exists(), s"Directory does not exist: $path")
    assert(dir.isDirectory, s"Path is not a directory: $path")
  }

}
