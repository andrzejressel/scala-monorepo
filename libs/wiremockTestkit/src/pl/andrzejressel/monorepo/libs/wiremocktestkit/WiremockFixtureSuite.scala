package pl.andrzejressel.monorepo.libs.wiremocktestkit

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

trait WiremockFixtureSuite { self: munit.FunSuite =>

  protected val wiremock: FunFixture[WireMockServer] = FunFixture(
    setup = { _ =>
      val server = new WireMockServer(
        wireMockConfig().bindAddress("127.0.0.1").dynamicPort()
      )
      server.start()
      server
    },
    teardown = { server =>
      server.stop()
    }
  )

}
