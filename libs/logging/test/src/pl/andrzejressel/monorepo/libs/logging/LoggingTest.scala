package pl.andrzejressel.monorepo.libs.logging

class LoggingTest extends munit.FunSuite {

  test("logger name matches the fully qualified name of the mixing class") {
    val user = new LoggingUser
    assertEquals(
      user.loggerName,
      "pl.andrzejressel.monorepo.libs.logging.LoggingTest$LoggingUser"
    )
  }

  private class LoggingUser extends Logging {
    def loggerName: String = protectedLogger.getName
  }

}
