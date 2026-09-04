package pl.andrzejressel.monorepo.libs.biedronka

import com.github.tomakehurst.wiremock.client.WireMock.*
import munit.FunSuite
import pl.andrzejressel.monorepo.libs.wiremocktestkit.WiremockFixtureSuite

import java.net.URI

class BiedronkaAuthTest extends FunSuite with WiremockFixtureSuite {

  test("parseTokensJson should decode JSON escapes") {
    val actual = BiedronkaAuth.parseTokensJson(
      """{"access_token":"access\"token","refresh_token":"refresh\u0020token"}"""
    )

    assertEquals(
      actual,
      BiedronkaTokens(
        AccessToken("access\"token"),
        RefreshToken("refresh token")
      )
    )
  }

  test("parseTokensJson should reject invalid token payloads") {
    Seq(
      "{",
      """{"access_token":"access"}""",
      """{"access_token":1,"refresh_token":"refresh"}"""
    ).foreach { body =>
      intercept[IllegalArgumentException](BiedronkaAuth.parseTokensJson(body))
    }
  }

  test("generateLoginURL should create authorization URL with PKCE challenge") {
    val verifier = "UuDmeH16R-2ogYBEn3-3p_OPuD8S74y8b19N0tP9FXY"
    val biedronka =
      BiedronkaAuth(URI("https://konto.biedronka.pl").toURL, verifier)

    val actual = biedronka.generateLoginURL()

    assertEquals(
      actual.toString,
      "https://konto.biedronka.pl/realms/loyalty/protocol/openid-connect/auth" +
        "?response_type=code" +
        "&client_id=cma20" +
        "&redirect_uri=app%3A%2F%2Fcma20.biedronka.pl" +
        "&code_challenge=uWSeqMpQlCbzSMhnie_4Ru1bOM7axX-0xn5Xddfc2go" +
        "&code_challenge_method=S256"
    )
  }

  wiremock.test(
    "createTokens should exchange authorization code using token endpoint"
  ) { wiremock =>
    wiremock.stubFor(
      post(urlEqualTo("/realms/loyalty/protocol/openid-connect/token"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""{"access_token":"access","refresh_token":"refresh"}""")
        )
    )

    val verifier = "test-verifier"
    val biedronka = BiedronkaAuth(URI(wiremock.baseUrl()).toURL, verifier)

    val actual = biedronka.createTokens(
      URI("app://cma20.biedronka.pl?code=authorization-code")
    )

    assertEquals(
      actual,
      BiedronkaTokens(
        accessToken = AccessToken("access"),
        refreshToken = RefreshToken("refresh")
      )
    )

    wiremock.verify(
      postRequestedFor(
        urlEqualTo("/realms/loyalty/protocol/openid-connect/token")
      )
        .withFormParam("grant_type", equalTo("authorization_code"))
        .withFormParam("client_id", equalTo("cma20"))
        .withFormParam("redirect_uri", equalTo("app://cma20.biedronka.pl"))
        .withFormParam("code", equalTo("authorization-code"))
        .withFormParam("code_verifier", equalTo("test-verifier"))
    )
  }

  wiremock.test(
    "refreshTokens should exchange refresh token using token endpoint"
  ) { wiremock =>
    wiremock.stubFor(
      post(urlEqualTo("/realms/loyalty/protocol/openid-connect/token"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{"access_token":"new-access","refresh_token":"new-refresh"}"""
            )
        )
    )

    val biedronka =
      BiedronkaAuth(URI(wiremock.baseUrl()).toURL, "unused-verifier")
    val actual = biedronka.refreshTokens(RefreshToken("old-refresh-token"))

    assertEquals(
      actual,
      BiedronkaTokens(
        accessToken = AccessToken("new-access"),
        refreshToken = RefreshToken("new-refresh")
      )
    )

    wiremock.verify(
      postRequestedFor(
        urlEqualTo("/realms/loyalty/protocol/openid-connect/token")
      )
        .withFormParam("grant_type", equalTo("refresh_token"))
        .withFormParam("client_id", equalTo("cma20"))
        .withFormParam("redirect_uri", equalTo("app://cma20.biedronka.pl"))
        .withFormParam("refresh_token", equalTo("old-refresh-token"))
    )
  }

  wiremock.test(
    "createTokens should fail when token endpoint returns non-2xx response"
  ) { wiremock =>
    wiremock.stubFor(
      post(urlEqualTo("/realms/loyalty/protocol/openid-connect/token"))
        .willReturn(
          aResponse()
            .withStatus(500)
            .withHeader("Content-Type", "application/json")
            .withBody("""{"error":"server_error"}""")
        )
    )

    val biedronka =
      BiedronkaAuth(URI(wiremock.baseUrl()).toURL, "test-verifier")

    val error = intercept[IllegalStateException] {
      biedronka.createTokens(
        URI("app://cma20.biedronka.pl?code=authorization-code")
      )
    }

    assert(error.getMessage.contains("status 500"))
  }

}
