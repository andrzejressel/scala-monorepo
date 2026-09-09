package pl.andrzejressel.monorepo.libs.wiremocktestkit

class WiremockFixtureSuiteSuite extends munit.FunSuite with WiremockFixtureSuite {
  wiremock.test("Should create web server") { server =>
    assert(server.isRunning)
  }
}
